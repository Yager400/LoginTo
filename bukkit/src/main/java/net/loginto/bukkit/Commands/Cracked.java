/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.LoginTo;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import net.loginto.bukkit.Utils.Premium.proxy.PremiumUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Cracked implements CommandExecutor {

    private final Plugin plugin;
    @SuppressWarnings("unused")
    private final Database database;
    private List<Player> playerList = new ArrayList<>();

    public Cracked(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        Player player = (Player) sender;

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin)) {
            player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_FEATURE_NOT_ENABLED.path(), player, plugin));
            return true;
        }

        if (!player.hasPermission("loginto.cracked")) {
            player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin));
            return true;
        }

        CompletableFuture.supplyAsync(() -> {
            return PremiumUtils.PlayerPremium.IsPlayerInThePremiumDB(player, plugin);
        }).thenAccept(isPremiumAlready -> {
            boolean skip = false;

            if (isPremiumAlready) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CRACKED_ERROR_ALREADY_CRACKED.path(), player, plugin));
                });
            } else {

                if (!playerList.contains(player)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        playerList.add(player);
                        player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CRACKED_WARN.path(), player, plugin));
                    });
                    skip = true;
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        playerList.remove(player);
                    });
                }

            }

            if (!skip) {
                PremiumUtils.PlayersInfo.sendCrackedRequest(player, plugin);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.CRACKED_DONE.path(), player, plugin));
                });
            }
        });


        return true;
    }
}
