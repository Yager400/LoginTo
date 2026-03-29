/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

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

        if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.feature-not-enabled", player, plugin));
            return true;
        }

        if (!player.hasPermission("loginto.cracked")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }
        
        CompletableFuture.supplyAsync(() -> {
            return PremiumUtils.PlayerPremium.IsPlayerInThePremiumDB(player, plugin);
        }).thenAccept(isPremiumAlready -> {
            boolean skip = false;

            if (isPremiumAlready) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.error.already-cracked", player, plugin));
                });
            }
            else {
                
                if (!playerList.contains(player)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        playerList.add(player);
                        sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.cracked-warn", player, plugin));
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
                    sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.cracked-done", player, plugin));
                });
            }
        });


        return true;
    }
}
