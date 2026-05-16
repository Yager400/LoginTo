/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import java.nio.charset.StandardCharsets;

import net.loginto.bungeecord.Database.Database;
import net.loginto.bungeecord.Utility.ClientBrandName;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class onPluginMessage implements Listener {
    
    private final Database database;
    private final ProxyServer server;

    public onPluginMessage(Database database, ProxyServer server) {
        this.database = database;
        this.server = server;
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

            if (tag.equalsIgnoreCase("minecraft:brand")) {
                byte[] data = event.getData().clone();
                String brand = new String(data, 1, data.length - 1, StandardCharsets.UTF_8);

                ClientBrandName cbn = new ClientBrandName(brand, player, server);
                cbn.check();
            }
        }
    }
}
