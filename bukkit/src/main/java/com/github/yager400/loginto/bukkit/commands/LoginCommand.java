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
import com.github.yager400.loginto.bukkit.playerutils.PlayerStatus;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.folia.FoliaLib;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LoginCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            Messages.sender.sendTextOrMessage("<red>Not a player", sender, null);
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        if (Sessions.isPlayerLogged(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_ALREADYLOGGEDIN), sender, null);
            return true;
        }

        if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_NOTREGISTERED), sender, null);
            return true;
        }

        String password = args[0];

        if (!LoginTo.getDatabase().isPasswordCorrect(player.getUniqueId(), password)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_WRONGPASSWORD), sender, null);
            int tries = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_MAXLOGINATTEMPTS);
            if (tries > 0) {
                Tries.incrementTries(player.getUniqueId());
                if (Tries.isPlayerOutOfTries(player.getUniqueId(), tries)) {
                    FoliaLib.get().kickPlayer(player, Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORFAILINGTHELOGIN)));
                    return true;
                }
            }
            return true;
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINSUCCESS), sender, null);
        PlayerStatus.setPlayerAsLogged(player);

        WebHooks.sendLoginWebhook(player.getName(), player.getUniqueId());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("<password>");
        }

        return list;
    }

}
