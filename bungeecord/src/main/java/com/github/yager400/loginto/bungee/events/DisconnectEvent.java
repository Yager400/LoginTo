/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.events;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.playerutils.PlayerStatus;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisconnectEvent implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        PlayerStatus.setPlayerAsNotLogged(event.getPlayer());

        ProxiedPlayer player = event.getPlayer();

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            LoginTo.getDatabase().updateSession(
                    player.getUniqueId(),
                    player.getAddress().getAddress().getHostAddress(),
                    LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SESSIONS_SESSIONDURATION) * 60 * 60
            );
        }

        /*
        ServerInfo preLoginServer = LoginTo.getInstance().getProxy().getServerInfo(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN));
        if (player.getServer() != null && player.getServer().getInfo() != preLoginServer) {
            player.connect(preLoginServer);
        }
         */
    }

}
