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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

public class delacc implements CommandExecutor, TabCompleter {
        
    private final Plugin plugin;
    private final Database database;

    public delacc(Plugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return false;
        }

        Player player = (Player) sender;
        

        if (!sender.hasPermission("loginto.delacc")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("delacc.error.delacc-usage", player, plugin));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        database.removePlayer(target.getPlayer());

        if (target.isOnline()) {
            target.getPlayer().kickPlayer(LoginToFiles.Messages.getMessage("delacc.admin-deleted-account", player, plugin));
        }

        if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) {
            PremiumUtils.PlayersInfo.sendRemovePremiumPlayerMessage(target, plugin);
        }

        sender.sendMessage(LoginToFiles.Messages.getMessage("delacc.account-deleted", player, plugin));
        
        return false;
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
