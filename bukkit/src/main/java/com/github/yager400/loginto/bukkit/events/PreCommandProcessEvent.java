/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.common.players.Sessions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PreCommandProcessEvent implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage().trim();

        if (!message.startsWith("/")) {
            return;
        }

        String command = message.substring(1).split(" ")[0];

        if (!Sessions.isPlayerLogged(player.getUniqueId())) {

            if (!command.equals("login") && !command.equals("register") && !command.equals("changepassword")) {
                event.setCancelled(true);
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_EXECUTECOMMANDPRELOGIN), player, null);
            }
        }
    }

}
