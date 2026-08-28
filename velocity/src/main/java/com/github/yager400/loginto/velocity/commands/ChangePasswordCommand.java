/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.commands;

import com.github.yager400.loginto.common.utils.SecurityUtils;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangePasswordCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        if (!(sender instanceof Player player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        if (args.length != 2) {
            Messages.sender.sendTextOrMessage("<white>Usage: /changepassword <newPassword> <oldPassword>", sender, null);
            return;
        }

        String newPassword = args[0];
        String oldPassword = args[1];

        LoginTo.getServer().getScheduler().buildTask(LoginTo.getInstance(), () -> {
            char[] characters = LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).toCharArray();
            if (characters.length > 0 && !SecurityUtils.PasswordSecurity.doesIncludeReqChars(newPassword, characters)) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%charactes%", new String(characters));
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_NOREQUIREDCHARACTERSERROR), sender, placeholders);
                return;
            }

            int min = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_PASSWORDLENGTH_MIN);
            int max = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_PASSWIRDLENGTH_MAX);
            if (!SecurityUtils.PasswordSecurity.matchesLengthRequirement(newPassword, min, max)) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%min_length%", String.valueOf(min));
                placeholders.put("%max_length%", String.valueOf(max));
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_PASSWORDLENGTHERROR), sender, placeholders);
                return;
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_NOTREGISTERED), sender, null);
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PASSWORD_DECLINEONCOMMONPASSWORD)) {
                if (SecurityUtils.PasswordSecurity.isCommon(newPassword, LoginTo.getDataDirectory(), player.getUsername())) {
                    Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_PASSWORDISTOOSIMPLE), sender, null);
                    return;
                }
            }

            if (LoginTo.getDatabase().isPasswordCorrect(player.getUniqueId(), oldPassword)) {
                LoginTo.getDatabase().updatePassword(player.getUniqueId(), newPassword);
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_PASSWORDCHANGED), sender, null);
            } else {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_WRONGOLDPASSWORD), sender, null);
            }
        }).schedule();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("loginto.changepassword");
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        List<String> list = new ArrayList<>();
        String[] args = invocation.arguments();

        if (args.length == 1) {
            list.add("<newPassword>");
        }
        if (args.length == 2) {
            list.add("<oldPassword>");
        }

        return list;
    }

}
