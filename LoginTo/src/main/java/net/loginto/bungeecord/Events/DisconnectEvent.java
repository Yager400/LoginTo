/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import net.loginto.bungeecord.Database.Database;

public class DisconnectEvent implements Listener {

    private final Database database;

    public DisconnectEvent(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        database.removePlayersInfo(event.getPlayer().getName());
    }
}