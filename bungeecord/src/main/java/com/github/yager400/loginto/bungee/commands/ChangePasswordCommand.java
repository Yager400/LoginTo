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
import com.github.yager400.loginto.common.utils.SecurityUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangePasswordCommand extends Command implements TabExecutor {

    public ChangePasswordCommand() {
        super("changepassword", "loginto.changepassword");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return;
        }

        if (args.length != 2) {
            Messages.sender.sendTextOrMessage("<white>Usage: /changepassword <newPassword> <oldPassword>", sender, null);
            return;
        }

        String newPassword = args[0];
        String oldPassword = args[1];

        LoginTo.getInstance().getProxy().getScheduler().runAsync(LoginTo.getInstance(), () -> {
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
                if (SecurityUtils.PasswordSecurity.isCommon(newPassword, LoginTo.getInstance().getDataFolder().toPath(), player.getName())) {
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
        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("<newPassword>");
        }
        if (args.length == 2) {
            list.add("<oldPassword>");
        }

        return list;
    }

}
