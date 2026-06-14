/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.PlayerUtils.PlayerMessages;
import net.loginto.bukkit.Database.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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
            PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_FEATURE_NOT_ENABLED.path(), player, plugin);
            return true;
        }

        if (!player.hasPermission("loginto.cracked")) {
            PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin);
            return true;
        }

        if (database.premiumTableContainsPlayer(player.getName()) && !database.isPremium(player.getName())) {
            PlayerMessages.player.sendMessage(MessageKeys.CRACKED_ERROR_ALREADY_CRACKED.path(), player, plugin);
            return true;
        }

        if (!playerList.contains(player)) {
            PlayerMessages.player.sendMessage(MessageKeys.CRACKED_WARN.path(), player, plugin);
            playerList.add(player);
            return true;
        }

        playerList.remove(player);
        database.updatePlayerAccountStatus(player.getName(), false);
        PlayerMessages.player.sendMessage(MessageKeys.CRACKED_DONE.path(), player, plugin);

        return true;
    }
}
