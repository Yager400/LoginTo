/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

public class OnPacketReceived implements PacketListener {
    
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (
            event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW ||
            event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE
            ) {

            if (isPlayerLogged(event.getPlayer())) return;

            event.setCancelled(true);
        }
    }

}