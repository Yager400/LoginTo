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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

public class premium implements CommandExecutor, TabCompleter {
        
    private final Plugin plugin;
    @SuppressWarnings("unused")
    private final Database database;
    private List<Player> playerList = new ArrayList<>();

    public premium(Plugin plugin, Database database) {
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

        if (!player.hasPermission("loginto.premium.me")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        if (args.length >= 1) {
            if (!player.hasPermission("loginto.premium.others")) {
                sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (!target.isOnline()) {
                sender.sendMessage("This player isn't online");
                return true;
            }

            if (PremiumUtils.PlayerPremium.IsPlayerInThePremiumDB(target, plugin)) {
                sender.sendMessage("Player already premium");
                return true;
            }

            PremiumUtils.PlayersInfo.sendPremiumPluginMessage(target, plugin);

            sender.sendMessage("§2This player is now premium");

        } else {
            if (PremiumUtils.PlayerPremium.IsPlayerInThePremiumDB(player, plugin)) {
                sender.sendMessage(LoginToFiles.Messages.getMessage("premium.error.already-premium", player, plugin));
                return true;
            }

            if (!playerList.contains(player)) {
                playerList.add(player);
                sender.sendMessage(LoginToFiles.Messages.getMessage("premium.premium-warn", player, plugin));
                return true;
            } else {
                playerList.remove(player);
            }

            PremiumUtils.PlayersInfo.sendPremiumPluginMessage(player, plugin);

            sender.sendMessage(LoginToFiles.Messages.getMessage("premium.premium-done", player, plugin));

        }   


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
            return list;
        }

        return null;
    }
}
