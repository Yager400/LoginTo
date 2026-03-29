/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.bungeecord.Database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class onPluginMessage implements Listener {
    
    private final Database database;

    public onPluginMessage(Database database) {
        this.database = database;
    }

    @EventHandler
    public void PluginMessage(PluginMessageEvent event) {
    if (event.getSender() instanceof ProxiedPlayer) {
        String tag = event.getTag();
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        
        if (
            tag.startsWith("velocity:") || 
            tag.equalsIgnoreCase("BungeeCord") ||
            tag.startsWith("bungeecord:")
        ) {
            if (!database.isPlayerLogged(player.getName())) {
                event.setCancelled(true);
            }
        }

    }
}
}
