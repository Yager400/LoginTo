/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CommandEvent implements Listener {

    @EventHandler
    public void onCommand(ChatEvent event) {

        String messageTrim = event.getMessage().trim();
        if (!messageTrim.startsWith("/")) {
            return;
        }
        messageTrim = messageTrim.replace("/", "");

        if (messageTrim.startsWith("login") ||
                messageTrim.startsWith("l") ||
                messageTrim.startsWith("register") ||
                messageTrim.startsWith("r")
        ) {
            return;
        }

        for (Object allowedCommand : LoginToFiles.Config.getList(ConfigKeys.COMMANDS_SETTINGS_PRE_LOGIN_ALLOWED_COMMANDS.path())) {
            if (messageTrim.equalsIgnoreCase(String.valueOf(allowedCommand))) {
                return;
            }
        }

        if (event.getSender() instanceof ProxiedPlayer player) {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                event.setCancelled(true);
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.ERRORS_ACTIVITY_BEFORE_LOGIN_ONCOMMAND.path()));
            }
        }
    }
}
