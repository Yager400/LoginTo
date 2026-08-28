/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.playerutils;

import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.UUID;

public class PlayerStatus {

    public static void setPlayerAsLogged(Player player) {
        Sessions.addPlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        String postLoginServerName = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN);
        if (!postLoginServerName.equals(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN))) {
            RegisteredServer postLoginServer = PlayerStatus.getRegisteredServer(postLoginServerName);
            if (postLoginServer != null && player.getCurrentServer().get() != postLoginServer) {
                player.createConnectionRequest(postLoginServer).connect();
            }
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().addPlayer(player.getUniqueId());
            }
        }
    }

    public static void setPlayerAsNotLogged(Player player) {
        Sessions.removePlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().removePlayer(player.getUniqueId());
            }
        }
    }

    public static void setPlayerAsLoggedViaEvent(PlayerChooseInitialServerEvent event) {
        Sessions.addPlayer(event.getPlayer().getUniqueId());

        Tries.removePlayer(event.getPlayer().getUniqueId());

        String postLoginServerName = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN);
        if (!postLoginServerName.equals(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN))) {
            RegisteredServer postLoginServer = PlayerStatus.getRegisteredServer(postLoginServerName);
            event.setInitialServer(postLoginServer);
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().addPlayer(event.getPlayer().getUniqueId());
            }
        }
    }

    public static void setPlayerAsNotLoggedViaEvent(PlayerChooseInitialServerEvent event) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());

        Tries.removePlayer(event.getPlayer().getUniqueId());

        RegisteredServer preLoginServer = PlayerStatus.getRegisteredServer(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN));
        event.setInitialServer(preLoginServer);

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().removePlayer(event.getPlayer().getUniqueId());
            }
        }
    }

    public static RegisteredServer getRegisteredServer(String serverName) {
        for (RegisteredServer registeredServer : LoginTo.getServer().getAllServers()) {
            if (registeredServer.getServerInfo().getName().equals(serverName)) {
                return registeredServer;
            }
        }
        return null;
    }

}
