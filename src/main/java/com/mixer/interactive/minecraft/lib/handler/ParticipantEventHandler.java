package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.participant.ParticipantJoinEvent;
import com.mixer.interactive.event.participant.ParticipantLeaveEvent;
import com.mixer.interactive.event.participant.ParticipantUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;

/**
 * Maintains the local cache of InteractiveParticipants by listening for join/update/leave participant events.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class ParticipantEventHandler extends AbstractEventHandler {

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
        event.getParticipants().forEach(p -> MixerInteractive.getParticipants().put(p.getSessionID(), p));
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
        event.getParticipants().forEach(p -> MixerInteractive.getParticipants().replace(p.getSessionID(), p));
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
        event.getParticipants().forEach(p -> MixerInteractive.getParticipants().remove(p.getSessionID()));
    }
}
