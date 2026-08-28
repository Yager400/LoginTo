/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.utils.SecurityUtils;
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
import java.util.HashMap;
import java.util.List;

public class RegisterCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (!(sender instanceof Player player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        if (args.length != 2) {
            Messages.sender.sendTextOrMessage("<white>Usage: /register <password> <confirmPassword>", sender, null);
            return;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_PASSWORDCONFIRMATIONMISMATCH), sender, null);
            return;
        }

        LoginTo.getServer().getScheduler().buildTask(LoginTo.getInstance(), () -> {
            char[] characters = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).toCharArray();
            if (characters.length > 0 && !SecurityUtils.PasswordSecurity.doesIncludeReqChars(password, characters)) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%characters%", new String(characters));
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_NOREQUIREDCHARACTERERROR), sender, placeholders);
                return;
            }

            int min = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_PASSWORDLENGTH_MIN);
            int max = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_PASSWIRDLENGTH_MAX);
            if (!SecurityUtils.PasswordSecurity.matchesLengthRequirement(password, min, max)) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%min_length%", String.valueOf(min));
                placeholders.put("%max_length%", String.valueOf(max));
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_PASSWORDLENGTHERROR), sender, placeholders);
                return;
            }

            if (LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_ALREADYREGISTERED), sender, null);
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PASSWORD_DECLINEONCOMMONPASSWORD)) {
                if (SecurityUtils.PasswordSecurity.isCommon(password, LoginTo.getDataDirectory(), player.getUsername())) {
                    Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_PASSWORDISTOOSIMPLE), sender, null);
                    return;
                }
            }

            LoginTo.getDatabase().insertPlayer(player.getUniqueId(), password, "", false, false, false, player.getRemoteAddress().getAddress().getHostAddress());

            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTRATIONSUCCESS), sender, null);
            PlayerStatus.setPlayerAsLogged(player);

            WebHooks.sendRegisterWebhook(player.getUsername(), player.getUniqueId());
        }).schedule();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.register");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            list.add("<password>");
        }
        if (args.length == 2) {
            list.add("<confirmPassword>");
        }

        return list;
    }

}
