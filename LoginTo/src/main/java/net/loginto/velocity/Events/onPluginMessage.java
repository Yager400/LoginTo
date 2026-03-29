/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;

import net.loginto.velocity.Database.Database;

public class onPluginMessage {

    private final Database database;

    public onPluginMessage(Database database) {
        this.database = database;
    }
    
    @Subscribe
    public void PluginMessage(PluginMessageEvent event) {
        if (event.getSource() instanceof Player) {
            Player player = (Player) event.getSource();
            String channel = event.getIdentifier().getId();
            
            if (
                channel.startsWith("velocity:") || 
                channel.equalsIgnoreCase("BungeeCord") ||
                channel.startsWith("bungeecord:")
            ) {
                if (!database.isPlayerLogged(player.getUsername())) {
                    event.setResult(PluginMessageEvent.ForwardResult.handled());
                }
            }
        }
    }
}
