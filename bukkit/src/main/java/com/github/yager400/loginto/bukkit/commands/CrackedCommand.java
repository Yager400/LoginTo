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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CrackedCommand implements CommandExecutor, TabCompleter {

    Set<Player> warnedPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1) {
            if (sender.hasPermission("loginto.cracked.other")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if (!target.hasPlayedBefore()) {
                    Messages.sender.sendTextOrMessage("<red>Invalid player", sender, null);
                    return true;
                }
                if (LoginTo.getDatabase().databaseContainsPlayer(target.getUniqueId())) {
                    CompletableFuture.runAsync(() -> {
                        LoginTo.getDatabase().updatePremium(target.getUniqueId(), false);
                        LoginTo.getDatabase().updateCracked(target.getUniqueId(), true);
                        Messages.sender.sendTextOrMessage("<green>This player is now cracked", sender, null);
                    });
                } else {
                    Messages.sender.sendTextOrMessage("<red>Player not registered", sender, null);
                }
                return true;
            }
            return false;
        }

        if (!(sender instanceof Player player)) {
            Messages.console.sendMessage("<red>Not a player", null);
            return true;
        }

        if (!warnedPlayers.contains(player)) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_CRACKEDWARN), sender, null);
            warnedPlayers.add(player);
            return true;
        } else {
            warnedPlayers.remove(player);
        }

        if (LoginTo.getDatabase().isCracked(player.getUniqueId())) {
            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_ALREADYCRACKED), sender, null);
            return true;
        }

        CompletableFuture.runAsync(() -> {
            LoginTo.getDatabase().updatePremium(player.getUniqueId(), false);
            LoginTo.getDatabase().updateCracked(player.getUniqueId(), true);

            Messages.sender.sendTextOrMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_CRACKED_CRACKEDSUCCESS), sender, null);
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                list.add(player.getName());
            }
        }

        return list;
    }

}
