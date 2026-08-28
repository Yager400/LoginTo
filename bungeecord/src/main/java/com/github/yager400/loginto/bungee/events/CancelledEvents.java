/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.events;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import com.github.yager400.loginto.common.players.Sessions;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CancelledEvents implements Listener {

    @EventHandler
    public void onServerPreConnect(ServerConnectedEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            ServerInfo authServerInfo = LoginTo.getInstance().getProxy().getServerInfo(
                    LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN)
            );
            if (!event.getPlayer().getServer().getInfo().getName().equals(authServerInfo.getName())) {
                event.getPlayer().connect(authServerInfo);
            }
        }
    }

    // Called by CommandEvent
    public static void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer player) {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                event.setCancelled(true);
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_CHATTINGBEFORELOGIN), player, null, false);
            }
        }
    }

}
