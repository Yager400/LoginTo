/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.common.PlayerUtils.Sessions;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Disconnect implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());
        Sessions.removeBorrowedData(event.getPlayer().getUniqueId());
    }
}
