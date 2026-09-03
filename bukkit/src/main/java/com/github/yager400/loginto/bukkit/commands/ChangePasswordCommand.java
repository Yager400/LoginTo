/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.commands;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.common.utils.SecurityUtils;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.folia.FoliaLib;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangePasswordCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            return false;
        }

        String newPassword = args[0];
        int otpCode;
        try {
            otpCode = Integer.parseInt(args[1]);
        } catch (Exception e) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_WRONGOTPCODE), sender, null);
            return true;
        }

        FoliaLib.get().runTaskAsync(() -> {
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

            try {
                GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
                String secret = LoginTo.getDatabase().getSecret(player.getUniqueId());
                if (secret == null || secret.isEmpty()) {
                    Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_NOOTPCODEFOUND), sender, null);
                    return;
                }

                if (googleAuthenticator.authorize(secret, otpCode)) {
                    LoginTo.getDatabase().updatePassword(player.getUniqueId(), newPassword);
                    Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_PASSWORDCHANGED), sender, null);
                } else {
                    Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.CHANGEPASSWORD_WRONGOTPCODE), sender, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("<newPassword>");
        }
        if (args.length == 2) {
            list.add("<otp_code>");
        }

        return list;
    }

}
