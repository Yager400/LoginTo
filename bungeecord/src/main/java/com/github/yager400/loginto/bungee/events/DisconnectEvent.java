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
import com.github.yager400.loginto.common.players.Sessions;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisconnectEvent implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            if (Sessions.isPlayerLogged(player.getUniqueId())) {
                LoginTo.getDatabase().updateSession(
                        player.getUniqueId(),
                        player.getAddress().getAddress().getHostAddress(),
                        LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SESSIONS_SESSIONDURATION) * 60 * 60
                );
            }
        }

        PlayerStatus.setPlayerAsNotLogged(event.getPlayer());
    }

}
