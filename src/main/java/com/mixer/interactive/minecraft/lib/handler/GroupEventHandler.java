package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.group.GroupCreateEvent;
import com.mixer.interactive.event.group.GroupDeleteEvent;
import com.mixer.interactive.event.group.GroupUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;

/**
 * Maintains the local cache of InteractiveGroups by listening for create/update/delete group events.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class GroupEventHandler extends AbstractEventHandler {

    /**
     * Adds all InteractiveGroups to the local cache that were created on the Mixer Interactive service.
     *
     * @param   event
     *          GroupCreateEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onGroupCreated(GroupCreateEvent event) {
        event.getGroups().forEach(g -> MixerInteractive.getGroups().put(g.getGroupID(), g));
    }

    /**
     * Updates all InteractiveGroups in the local cache that were updated on the Mixer Interactive service.
     *
     * @param   event
     *          GroupUpdateEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onGroupUpdated(GroupUpdateEvent event) {
        event.getGroups().forEach(g -> MixerInteractive.getGroups().replace(g.getGroupID(), g));
    }

    /**
     * Removes all InteractiveGroups from the local cache that were deleted on the Mixer Interactive service.
     *
     * @param   event
     *          GroupDeleteEvent
     *
     * @since   1.0.0
     */
    @Subscribe
    public void onGroupDeleted(GroupDeleteEvent event) {
        MixerInteractive.getGroups().remove(event.getGroupID());
    }
}
