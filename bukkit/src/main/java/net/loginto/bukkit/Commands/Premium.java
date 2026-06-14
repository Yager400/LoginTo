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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Premium implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    @SuppressWarnings("unused")
    private final Database database;
    private List<Player> playerList = new ArrayList<>();

    public Premium(Plugin plugin, Database database) {
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

        if (!player.hasPermission("loginto.premium.me")) {
            PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin);
            return true;
        }

        if (args.length >= 1) {
            if (!player.hasPermission("loginto.premium.others")) {
                PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (!target.isOnline()) {
                player.sendMessage("This player isn't online");
                return true;
            }

            if (database.isPremium(target.getName())) {
                player.sendMessage("§cThis player is already premium");
                return true;
            }

            database.updatePlayerAccountStatus(player.getName(), true);

            player.sendMessage("§2This player is now premium");

        } else {

            if (database.isPremium(player.getName())) {
                PlayerMessages.player.sendMessage(MessageKeys.PREMIUM_ERROR_ALREADY_PREMIUM.path(), player, plugin);
                return true;
            }

            if (!playerList.contains(player)) {
                PlayerMessages.player.sendMessage(MessageKeys.PREMIUM_WARN.path(), player, plugin);
                playerList.add(player);
                return true;
            }

            playerList.remove(player);

            database.updatePlayerAccountStatus(player.getName(), true);

            PlayerMessages.player.sendMessage(MessageKeys.PREMIUM_DONE.path(), player, plugin);

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
