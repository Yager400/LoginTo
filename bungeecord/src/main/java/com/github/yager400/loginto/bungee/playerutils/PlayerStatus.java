/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.playerutils;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class PlayerStatus {

    public static void setPlayerAsLogged(ProxiedPlayer player) {
        Sessions.addPlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        String postLoginServerName = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN);
        if (!postLoginServerName.equals(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN))) {
            ServerInfo postLoginServer = LoginTo.getInstance().getProxy().getServerInfo(postLoginServerName);
            if (player.getServer() != null && player.getServer().getInfo() != postLoginServer) {
                player.connect(postLoginServer);
            }
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().addPlayer(player.getUniqueId());
            }
        }
    }

    public static void setPlayerAsNotLogged(ProxiedPlayer player) {
        Sessions.removePlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().removePlayer(player.getUniqueId());
            }
        }
    }

    public static void setPlayerAsLoggedViaEvent(ServerConnectEvent event) {
        Sessions.addPlayer(event.getPlayer().getUniqueId());

        Tries.removePlayer(event.getPlayer().getUniqueId());

        String postLoginServerName = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN);
        if (!postLoginServerName.equals(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN))) {
            ServerInfo postLoginServer = LoginTo.getInstance().getProxy().getServerInfo(postLoginServerName);
            event.setTarget(postLoginServer);
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().addPlayer(event.getPlayer().getUniqueId());
            }
        }
    }

    public static void setPlayerAsNotLoggedViaEvent(ServerConnectEvent event) {
        Sessions.removePlayer(event.getPlayer().getUniqueId());

        Tries.removePlayer(event.getPlayer().getUniqueId());

        ServerInfo preLoginServer = LoginTo.getInstance().getProxy().getServerInfo(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN));
        event.setTarget(preLoginServer);

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            if (LoginTo.getDatabaseBridge() != null) {
                LoginTo.getDatabaseBridge().removePlayer(event.getPlayer().getUniqueId());
            }
        }
    }
}
