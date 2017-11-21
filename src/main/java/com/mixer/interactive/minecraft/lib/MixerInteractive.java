package com.mixer.interactive.minecraft.lib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mixer.interactive.GameClient;
import com.mixer.interactive.minecraft.lib.handler.AbstractEventHandler;
import com.mixer.interactive.minecraft.lib.handler.GroupEventHandler;
import com.mixer.interactive.minecraft.lib.handler.ParticipantEventHandler;
import com.mixer.interactive.minecraft.lib.handler.SceneEventHandler;
import com.mixer.interactive.resources.control.InteractiveControl;
import com.mixer.interactive.resources.group.InteractiveGroup;
import com.mixer.interactive.resources.participant.InteractiveParticipant;
import com.mixer.interactive.resources.scene.InteractiveScene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MixerInteractive provides a singleton that contains a game client for which other mods can use to manage their Interactive
 * game logic. It also provides caching functionality for Interactive participants, groups, and scenes so that modders need
 * not constantly query Mixer Interactive for the state of these objects.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class MixerInteractive {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Singleton instance of Mixer Interactive
     */
    public static final MixerInteractive INSTANCE = new MixerInteractive();

    /**
     * Game client
     */
    private GameClient gameClient;

    /**
     * Locally cached copy of all participants for the currently connected game client
     */
    private Set<InteractiveParticipant> participants = Sets.newConcurrentHashSet();

    /**
     * Locally cached copy of all scenes for the currently connected game client
     */
    private Set<InteractiveScene> scenes = Sets.newConcurrentHashSet();

    /**
     * Locally cached copy of all groups for the currently connected game client
     */
    private Set<InteractiveGroup> groups = Sets.newConcurrentHashSet();

    /**
     * List of default event handlers to be registered with game clients
     */
    private List<AbstractEventHandler> defaultEventHandlers = new ArrayList<>();

    /**
     * List of custom event handlers to be registered with game clients
     */
    private List<AbstractEventHandler> customEventHandlers = new ArrayList<>();

    /**
     * Constructor.
     *
     * @since   1.0.0
     */
    private MixerInteractive() {
        defaultEventHandlers.add(new ParticipantEventHandler());
        defaultEventHandlers.add(new GroupEventHandler());
        defaultEventHandlers.add(new SceneEventHandler());
    }

    /**
     * Returns the game client.
     *
     * @return  The game client
     *
     * @since   1.0.0
     */
    public static GameClient getGameClient() {
        return INSTANCE.gameClient;
    }

    /**
     * Builds a new game client for the specified project version id. Any previous game client is discarded. Default and
     * custom event handlers are registered for the new game client.
     *
     * @param   projectVersionId
     *          Project verison id that the new game client will use
     *
     * @return  The new game client
     *
     * @since   1.0.0
     */
    public static GameClient initGameClient(int projectVersionId) {
        LOG.debug("Building new game client with projectVersionId={}", projectVersionId);
        INSTANCE.gameClient = new GameClient(projectVersionId);
        for (AbstractEventHandler defaultHandler : INSTANCE.defaultEventHandlers) {
            LOG.debug("Registering default event handler '{}' to game client for project id {}", defaultHandler, projectVersionId);
            INSTANCE.gameClient.getEventBus().register(defaultHandler);
        }
        for (AbstractEventHandler customHandler : INSTANCE.customEventHandlers) {
            LOG.debug("Registering custom event handler '{}' to game client for project id {}", customHandler, projectVersionId);
            INSTANCE.gameClient.getEventBus().register(customHandler);
        }
        return INSTANCE.gameClient;
    }

    /**
     * Returns a List of default event handlers.
     *
     * @return  List of default event handlers
     *
     * @since   1.0.0
     */
    public static List<AbstractEventHandler> getDefaultEventHandlers() {
        return ImmutableList.copyOf(INSTANCE.defaultEventHandlers);
    }

    /**
     * Returns a List of custom event handlers.
     *
     * @return  List of custom event handlers
     *
     * @since   1.0.0
     */
    public static List<AbstractEventHandler> getCustomEventHandlers() {
        return INSTANCE.customEventHandlers;
    }

    /**
     * Returns the locally cached Set of participants.
     *
     * @return  Set of InteractiveParticipants
     *
     * @since   1.0.0
     */
    public static Set<InteractiveParticipant> getParticipants() {
        return INSTANCE.participants;
    }

    /**
     * Returns the locally cached Set of scenes.
     *
     * @return  Set of InteractiveScenes
     *
     * @since   1.0.0
     */
    public static Set<InteractiveScene> getScenes() {
        return INSTANCE.scenes;
    }

    /**
     * Returns the locally cached Set of groups.
     *
     * @return  Set of InteractiveGroups
     *
     * @since   1.0.0
     */
    public static Set<InteractiveGroup> getGroups() {
        return INSTANCE.groups;
    }

    /**
     * Iterates through all locally cached scenes and returns a set of all controls within them.
     *
     * @return  Set of InteractiveControls
     *
     * @since   1.0.0
     */
    public static Set<InteractiveControl> getControls() {
        Set<InteractiveControl> controls = new HashSet<>();
        INSTANCE.scenes.forEach(scene -> controls.addAll(scene.getControls()));
        return controls;
    }
}
