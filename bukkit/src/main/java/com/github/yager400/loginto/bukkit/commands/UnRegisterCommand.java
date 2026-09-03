/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.commands;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.folia.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnRegisterCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getName().equals(args[0])) {
                Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_CANNOTUNREGISTERYOURSELF), sender, null);
                return true;
            }
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_INVALIDPLAYER), sender, null);
            return true;
        }

        LoginTo.getDatabase().removePlayerRecord(target.getUniqueId());

        if (target.isOnline()) {
            FoliaLib.get().kickPlayer(target.getPlayer(), Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_UNREGISTERSUCCESS)));
        }

        Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.UNREGISTER_ADMINUNREGISTERSUCCESS), sender, null);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            WebHooks.sendUnRegisterWebhook(player.getName(), player.getUniqueId(), target.getName(), target.getUniqueId());
        } else {
            WebHooks.sendUnRegisterWebhook("Console", new UUID(0L, 0L), target.getName(), target.getUniqueId());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            }
        }

        return list;
    }

}
