package com.mixer.interactive.minecraft.lib.handler;

import com.google.common.eventbus.Subscribe;
import com.mixer.interactive.event.group.GroupCreateEvent;
import com.mixer.interactive.event.group.GroupDeleteEvent;
import com.mixer.interactive.event.group.GroupUpdateEvent;
import com.mixer.interactive.minecraft.lib.MixerInteractive;
import com.mixer.interactive.resources.group.InteractiveGroup;

import java.util.HashSet;
import java.util.Set;

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
        MixerInteractive.getGroups().addAll(event.getGroups());
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
        Set<InteractiveGroup> groupsToUpdate = new HashSet<>();
        for (InteractiveGroup group : event.getGroups()) {
            for (InteractiveGroup cachedGroup : MixerInteractive.getGroups()) {
                if (cachedGroup.getGroupID().equals(group.getGroupID())) {
                    groupsToUpdate.add(cachedGroup);
                }
            }
        }
        MixerInteractive.getGroups().removeAll(groupsToUpdate);
        MixerInteractive.getGroups().addAll(event.getGroups());
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
        Set<InteractiveGroup> groups = new HashSet<>();
        for (InteractiveGroup group : MixerInteractive.getGroups()) {
            if (group.getGroupID().equals(event.getGroupID())) {
                groups.add(group);
            }
        }
        MixerInteractive.getGroups().removeAll(groups);
    }
}
