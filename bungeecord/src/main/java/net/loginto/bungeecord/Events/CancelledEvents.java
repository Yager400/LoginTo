/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CancelledEvents implements Listener {

    private final ProxyServer server;

    public CancelledEvents(ProxyServer server) {
        this.server = server;
    }

    @EventHandler
    public void onServerPreConnect(ServerConnectEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVERS_AUTH_SERVERS.path())) {
                ServerInfo serverInfo = server.getServerInfo(serverObject.toString());

                if (!event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(serverInfo.getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
