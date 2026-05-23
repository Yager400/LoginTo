/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UnRegister implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Database database;

    public UnRegister(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("loginto.unregister")) {
            if (sender instanceof Player player) {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin));
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), plugin));
            }
            return true;
        }

        if (args.length != 2) {
            if (sender instanceof Player player) {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_USAGE.path(), player, plugin));
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_USAGE.path(), plugin));
            }
            return true;
        }

        if (!args[1].equalsIgnoreCase("confirm")) {
            if (sender instanceof Player player) {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_NOT_CONFIRMED.path(), player, plugin));
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_NOT_CONFIRMED.path(), plugin));
            }
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            if (sender instanceof Player player) {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path(), player, plugin));
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path(), plugin));
            }
            return true;
        }

        boolean success = database.removePlayer(target.getName());

        if (!success) {
            if (sender instanceof Player player) {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path(), player, plugin));
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ERROR_PLAYER_DOESNT_EXIST.path(), plugin));
            }
            return true;
        }

        if (target.isOnline()) {
            if (sender instanceof Player player) {
                target.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ADMIN_UNREGISTERED_ACCOUNT.path(), player, plugin));
            } else {
                target.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ADMIN_UNREGISTERED_ACCOUNT.path(), plugin));
            }
        }

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin)) {
            CompletableFuture.runAsync(() -> {
                PremiumUtils.PlayersInfo.sendRemovePremiumRequest(target, plugin);
            });
        }

        if (sender instanceof Player player) {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ACCOUNT_UNREGISTERED.path(), player, plugin));
        } else {
            sender.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.UNREGISTER_ACCOUNT_UNREGISTERED.path(), plugin));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                list.add(p.getName());
            }
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirm>");
            return list;
        }

        return null;
    }
}
