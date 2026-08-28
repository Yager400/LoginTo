/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.github.yager400.loginto.velocity.playerutils.PlayerStatus;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;

public class LoginCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (!(sender instanceof Player player)) {
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
                    player.disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORFAILINGTHELOGIN)));
                    return;
                }
            }
            return;
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINSUCCESS), sender, null);
        PlayerStatus.setPlayerAsLogged(player);

        WebHooks.sendLoginWebhook(player.getUsername(), player.getUniqueId());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.login");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            list.add("<password>");
        }

        return list;
    }

}
