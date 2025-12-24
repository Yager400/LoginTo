/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Config.isFeatureEnabled;
import static net.loginto.bukkit.Configuration.Messages.getMessage;
import static net.loginto.bukkit.Premium.Cracked.*;

public class Cracked implements CommandExecutor {

    private final Plugin plugin;
    
    public Cracked(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        if (!sender.hasPermission("loginto.cracked.me")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return false;
        }

        if (isFeatureEnabled("premium.premium", plugin)){
            executeCrackedCommand((Player) sender, plugin);
        } else {
            sender.sendMessage(getMessage("errors.feature_not_enabled", plugin));
        }
            
        return true;
    }
    
}
