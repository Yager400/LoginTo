/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

public class cracked implements CommandExecutor {
        
    private final Plugin plugin;
    @SuppressWarnings("unused")
    private final Database database;
    private List<Player> playerList = new ArrayList<>();

    public cracked(Plugin plugin, Database database) {
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

        if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.feature-not-enabled", player, plugin));
            return true;
        }

        if (!player.hasPermission("loginto.cracked")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        if (PremiumUtils.PlayerPremium.IsPlayerInThePremiumDB(player, plugin)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.error.already-cracked", player, plugin));
            return true;
        }

        if (!playerList.contains(player)) {
            playerList.add(player);
            sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.cracked-warn", player, plugin));
            return true;
        } else {
            playerList.remove(player);
        }

        PremiumUtils.PlayersInfo.sendCrackedPluginMessage(player, plugin);

        sender.sendMessage(LoginToFiles.Messages.getMessage("cracked.cracked-done", player, plugin));


        return true;
    }
}
