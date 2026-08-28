/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;

import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.playerutils.PlayerStatus;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class CancelledEvents {

    @Subscribe
    public void onServerPreConnect(ServerConnectedEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            RegisteredServer authServer = PlayerStatus.getRegisteredServer(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN));
            ServerConnection connection = event.getPlayer().getCurrentServer().orElse(null);
            if (authServer != null && connection != null && connection.getServer() != authServer) {
                event.getPlayer().createConnectionRequest(authServer).connect();
            }
        }
    }

}
