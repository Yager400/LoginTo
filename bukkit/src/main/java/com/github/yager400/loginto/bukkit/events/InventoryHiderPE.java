/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.yager400.loginto.common.players.Sessions;
import org.bukkit.entity.Player;

public class InventoryHiderPE implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW ||
                event.getPacketType() == PacketType.Play.Client.HELD_ITEM_CHANGE) {

            Player player = event.getPlayer();
            if (Sessions.isPlayerLogged(player.getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT ||
                event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW ||
                event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS ||
                event.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) {

            Player player = event.getPlayer();
            if (Sessions.isPlayerLogged(player.getUniqueId())) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
