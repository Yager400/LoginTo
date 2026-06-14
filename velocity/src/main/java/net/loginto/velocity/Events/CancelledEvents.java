/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;

import java.util.Optional;

public class CancelledEvents {

    private final ProxyServer server;

    public CancelledEvents(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            for (Object serverObject : LoginToFiles.Config.getList(ConfigKeys.SERVERS_AUTH_SERVERS.path())) {
                Optional<RegisteredServer> registeredServer = server.getServer(serverObject.toString());

                if (registeredServer.isPresent()) {
                    if (
                            !event.getOriginalServer().getServerInfo().getName().equalsIgnoreCase(registeredServer.get().getServerInfo().getName())
                    ) {
                        event.setResult(ServerPreConnectEvent.ServerResult.denied());
                        return;
                    }
                }
            }
        }
    }

}
