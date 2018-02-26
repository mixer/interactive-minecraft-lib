package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.control.ControlCreateEvent;
import com.mixer.interactive.event.control.ControlDeleteEvent;
import com.mixer.interactive.event.control.ControlUpdateEvent;
import com.mixer.interactive.event.scene.SceneCreateEvent;
import com.mixer.interactive.event.scene.SceneDeleteEvent;
import com.mixer.interactive.event.scene.SceneUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;
import com.mixer.interactive.resources.control.InteractiveControl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Maintains the local cache of InteractiveScenes by listening for create/update/delete scene events.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class SceneEventHandler extends AbstractEventHandler {

    /**
     * Adds all InteractiveScenes to the local cache that were created on the Mixer Interactive service.
     *
     * @param   event
     *          SceneCreateEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onSceneCreated(SceneCreateEvent event) {
        event.getScenes().forEach(s -> MixerInteractive.getScenes().put(s.getSceneID(), s));
    }

    /**
     * Updates all InteractiveScenes in the local cache that were updated on the Mixer Interactive service.
     *
     * @param   event
     *          SceneUpdateEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onSceneUpdated(SceneUpdateEvent event) {
        event.getScenes().forEach(s -> MixerInteractive.getScenes().replace(s.getSceneID(), s));
    }

    /**
     * Removes all InteractiveScenes from the local cache that were deleted on the Mixer Interactive service.
     *
     * @param   event
     *          SceneDeleteEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onSceneDeleted(SceneDeleteEvent event) {
        MixerInteractive.getScenes().remove(event.getSceneID());
    }

    /**
     * Adds all InteractiveControls to the local cache that were created on the Mixer Interactive service.
     *
     * @param   event
     *          ControlCreateEvent
     *
     * @since   1.1.0
     */
    @Subscribe
    public void onControlCreated(ControlCreateEvent event) {
        synchronized (this) {
            if (MixerInteractive.getScenes().get(event.getSceneID()) != null) {
                MixerInteractive.getScenes().get(event.getSceneID()).getControls().removeAll(event.getControls());
                MixerInteractive.getScenes().get(event.getSceneID()).getControls().addAll(event.getControls());
            }
        }
    }

    /**
     * Updates all InteractiveControls in the local cache that were updated on the Mixer Interactive service.
     *
     * @param   event
     *          ControlUpdateEvent
     *
     * @since   1.1.0
     */
    @Subscribe
    public void onControlUpdated(ControlUpdateEvent event) {
        synchronized (this) {
            if (MixerInteractive.getScenes().get(event.getSceneID()) != null) {
                MixerInteractive.getScenes().get(event.getSceneID()).getControls().removeAll(event.getControls());
                MixerInteractive.getScenes().get(event.getSceneID()).getControls().addAll(event.getControls());
            }
        }
    }

    /**
     * Removes all InteractiveControls from the local cache that were deleted on the Mixer Interactive service.
     *
     * @param   event
     *          ControlDeleteEvent
     *
     * @since   1.1.0
     */
    @Subscribe
    public void onControlDeleted(ControlDeleteEvent event) {
        synchronized (this) {
            if (MixerInteractive.getScenes().get(event.getSceneID()) != null) {
                Set<InteractiveControl> controls = MixerInteractive.getScenes().get(event.getSceneID()).getControls()
                        .stream()
                        .filter(c -> event.getControlIds().contains(c.getControlID()))
                        .collect(Collectors.toSet());
                MixerInteractive.getScenes().get(event.getSceneID()).getControls().removeAll(controls);
            }
        }
    }
}
