/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bungeecord.Events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import net.loginto.bungeecord.Database.Database;

public class CommandEvent implements Listener {

    public final Database database;

    public CommandEvent(Database database) {
        this.database = database;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!event.getMessage().startsWith("/")) return;

        if (
            event.getMessage().startsWith("/login") || 
            event.getMessage().startsWith("/l") ||
            event.getMessage().startsWith("/register") ||
            event.getMessage().startsWith("/r")
        ) return;

        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            if (!database.isPlayerLogged(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
}