/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;

public class CommandEvent {

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {

        if (event.getCommand().startsWith("login") ||
                event.getCommand().startsWith("l") ||
                event.getCommand().startsWith("register") ||
                event.getCommand().startsWith("r")
        ) {
            return;
        }

        for (Object allowedCommand : LoginToFiles.Config.getList(ConfigKeys.COMMANDS_SETTINGS_PRE_LOGIN_ALLOWED_COMMANDS.path())) {
            if (event.getCommand().equalsIgnoreCase(String.valueOf(allowedCommand))) {
                return;
            }
        }

        if (event.getCommandSource() instanceof Player) {
            Player player = (Player) event.getCommandSource();
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_ACTIVITY_BEFORE_LOGIN_ONCOMMAND.path()));
            }
        }
    }
}
