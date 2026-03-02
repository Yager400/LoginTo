/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;


public class OnPacketSend implements PacketListener {
    
    @Override
    public void onPacketSend(PacketSendEvent event) {

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT ||
            event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW ||
            event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            
            if (isPlayerLogged(event.getPlayer())) return;

            event.setCancelled(true);

        }
    }
}
