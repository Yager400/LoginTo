/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.PlayerUtils.Logs;
import net.loginto.bukkit.Utils.LoginToFiles;

public class getlogs implements CommandExecutor, TabCompleter {
        
    private final Plugin plugin;

    public getlogs(Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("loginto.getlogs")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", null, plugin));
            return false;
        }

        if (!LoginToFiles.Config.isFeatureEnabled("logging.logging", plugin)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.feature-not-enabled", null, plugin));
            return false;
        }

        if (args.length < 1 || args.length > 2) {
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        

        List<String> data;

        if (args.length != 2) {
            data = Logs.getLogs(target, plugin, "n");
        } else {
            data = Logs.getLogs(target, plugin, args[1]);
        }

        if (data.size() <= 0) {
            sender.sendMessage("No logs for " + args[0]);
        } else {
            sender.sendMessage("§l" + args[0] + "§r logs:\n-----\n");
            for (String i : data) {
                String format = i + "\n-----\n";
                sender.sendMessage(format);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();

            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                p.getName();
            }

            return list;
        }

        if (args.length == 2) {
            List<String> list = new ArrayList<>();

            list.add("<dd/MM/yyyy>");

            return list;
        }

        return null;
    }
}
