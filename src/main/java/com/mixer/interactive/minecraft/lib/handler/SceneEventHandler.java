package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.scene.SceneCreateEvent;
import com.mixer.interactive.event.scene.SceneDeleteEvent;
import com.mixer.interactive.event.scene.SceneUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;
import com.mixer.interactive.resources.scene.InteractiveScene;

import java.util.HashSet;
import java.util.Set;

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
        MixerInteractive.getScenes().addAll(event.getScenes());
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
        Set<InteractiveScene> scenesToUpdate = new HashSet<>();
        for (InteractiveScene scene : event.getScenes()) {
            for (InteractiveScene cachedScene : MixerInteractive.getScenes()) {
                if (cachedScene.getSceneID().equals(scene.getSceneID())) {
                    scenesToUpdate.add(cachedScene);
                }
            }
        }
        MixerInteractive.getScenes().removeAll(scenesToUpdate);
        MixerInteractive.getScenes().addAll(event.getScenes());
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
        Set<InteractiveScene> scenes = new HashSet<>();
        for (InteractiveScene scene : MixerInteractive.getScenes()) {
            if (scene.getSceneID().equals(event.getSceneID())) {
                scenes.add(scene);
            }
        }
        MixerInteractive.getGroups().removeAll(scenes);
    }
}
