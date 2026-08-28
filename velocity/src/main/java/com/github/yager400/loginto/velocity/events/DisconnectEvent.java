/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;


import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.playerutils.PlayerStatus;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;

public class DisconnectEvent {

    @Subscribe
    public void onDisconnect(com.velocitypowered.api.event.connection.DisconnectEvent event) {
        PlayerStatus.setPlayerAsNotLogged(event.getPlayer());

        Player player = event.getPlayer();

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            LoginTo.getDatabase().updateSession(
                    player.getUniqueId(),
                    player.getRemoteAddress().getAddress().getHostAddress(),
                    LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SESSIONS_SESSIONDURATION) * 60 * 60
            );
        }
    }

}
