/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.commands;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import com.github.yager400.loginto.bungee.playerutils.PlayerStatus;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.WebHooks;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class LoginCommand extends Command implements TabExecutor {

    public LoginCommand() {
        super("login", "loginto.login", "l");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        if (args.length != 1) {
            Messages.sender.sendTextOrMessage("<white>Usage: /login <password>", sender, null);
            return;
        }

        if (Sessions.isPlayerLogged(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_ALREADYLOGGEDIN), sender, null);
            return;
        }

        if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_NOTREGISTERED), sender, null);
            return;
        }

        String password = args[0];

        if (!LoginTo.getDatabase().isPasswordCorrect(player.getUniqueId(), password)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_WRONGPASSWORD), sender, null);
            int tries = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_MAXLOGINATTEMPTS);
            if (tries > 0) {
                Tries.incrementTries(player.getUniqueId());
                if (Tries.isPlayerOutOfTries(player.getUniqueId(), tries)) {
                    player.disconnect(TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORFAILINGTHELOGIN))));
                    return;
                }
            }
            return;
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINSUCCESS), sender, null);
        PlayerStatus.setPlayerAsLogged(player);

        WebHooks.sendLoginWebhook(player.getName(), player.getUniqueId());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("<password>");
        }

        return list;
    }

}
