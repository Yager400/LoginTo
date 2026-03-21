/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

import net.loginto.bukkit.Events.Listeners.OnPacketReceived;
import net.loginto.bukkit.Events.Listeners.OnPacketSend;

public class Listener {

    public static void implementPacketEventListener() {
        EventManager events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new OnPacketSend(), PacketListenerPriority.HIGHEST);
        events.registerListener(new OnPacketReceived(), PacketListenerPriority.HIGHEST);
    }
}
