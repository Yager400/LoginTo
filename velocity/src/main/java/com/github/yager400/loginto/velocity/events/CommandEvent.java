/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;

import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;

public class CommandEvent {

    @Subscribe
    public void onPreLoginCommandExecution(CommandExecuteEvent event) {

        String command = event.getCommand();
        if (command.startsWith("login") || command.startsWith("register")) {
            return;
        }

        if (event.getCommandSource() instanceof Player player) {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_EXECUTECOMMANDPRELOGIN), player, null, false);
            }
        }

    }

}
