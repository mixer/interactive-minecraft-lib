package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.participant.ParticipantJoinEvent;
import com.mixer.interactive.event.participant.ParticipantLeaveEvent;
import com.mixer.interactive.event.participant.ParticipantUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;
import com.mixer.interactive.resources.participant.InteractiveParticipant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Maintains the local cache of InteractiveParticipants by listening for join/update/leave participant events.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class ParticipantEventHandler extends AbstractEventHandler {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Adds all InteractiveParticipants to the local cache that joined on the Mixer Interactive service.
     *
     * @param   event
     *          ParticipantJoinEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onParticipantJoined(ParticipantJoinEvent event) {
        MixerInteractive.getParticipants().addAll(event.getParticipants());
    }

    /**
     * Updates all InteractiveParticipants in the local cache that were updated on the Mixer Interactive service.
     *
     * @param   event
     *          ParticipantUpdateEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onParticipantUpdated(ParticipantUpdateEvent event) {
        Set<InteractiveParticipant> participantsToUpdate = new HashSet<>();
        for (InteractiveParticipant participant : event.getParticipants()) {
            for (InteractiveParticipant cachedParticipant : MixerInteractive.getParticipants()) {
                if (cachedParticipant.getSessionID().equals(participant.getSessionID())) {
                    participantsToUpdate.add(cachedParticipant);
                }
            }
        }
        MixerInteractive.getParticipants().removeAll(participantsToUpdate);
        MixerInteractive.getParticipants().addAll(event.getParticipants());
    }

    /**
     * Removes all InteractiveParticipants from the local cache that left the Mixer Interactive service.
     *
     * @param   event
     *          ParticipantLeaveEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onParticipantLeft(ParticipantLeaveEvent event) {
        LOG.debug(event);
        Set<InteractiveParticipant> participantsToUpdate = new HashSet<>();
        for (InteractiveParticipant participant : event.getParticipants()) {
            for (InteractiveParticipant cachedParticipant : MixerInteractive.getParticipants()) {
                if (cachedParticipant.getSessionID().equals(participant.getSessionID())) {
                    participantsToUpdate.add(cachedParticipant);
                }
            }
        }
        MixerInteractive.getParticipants().removeAll(participantsToUpdate);
    }
}
