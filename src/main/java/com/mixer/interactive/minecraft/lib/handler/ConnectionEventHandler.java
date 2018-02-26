package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.connection.ConnectionClosedEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;

/**
 * Maintains the local cache by listening for connection related events.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.1.0
 */
public class ConnectionEventHandler extends AbstractEventHandler {

    /**
     * TODO Finish Javadoc.
     *
     * @param   event
     *          ConnectionClosedEvent
     *
     * @since   1.1.0
     */
    @Subscribe
    public void onConnectionClosed(ConnectionClosedEvent event) {
        MixerInteractive.getScenes().clear();
        MixerInteractive.getGroups().clear();
        MixerInteractive.getParticipants().clear();
    }
}
