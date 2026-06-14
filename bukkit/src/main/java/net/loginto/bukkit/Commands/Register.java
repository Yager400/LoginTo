/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.PlayerUtils.PasswordSecurity;
import net.loginto.bukkit.PlayerUtils.PlayerMessages;
import net.loginto.bukkit.PlayerUtils.PlayerStatus;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Database database;

    public Register(Plugin plugin, Database database) {
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

        if (!sender.hasPermission("loginto.register")) {
            PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin);
            return true;
        }

        if (args.length != 2) {
            PlayerMessages.player.sendMessage(MessageKeys.REGISTER_ERROR_USAGE.path(), player, plugin);
            return true;
        }

        String password = args[0];

        String repetePassword = args[1];

        if (!password.equals(repetePassword)) {
            PlayerMessages.player.sendMessage(MessageKeys.REGISTER_ERROR_PASSWORD_MISMATCH.path(), player, plugin);
            return true;
        }

        if (!PasswordSecurity.doesIncludeReqChars(password, plugin)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%characters%", LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path(), plugin));
            PlayerMessages.player.sendMessage(MessageKeys.REGISTER_ERROR_CHARACTER_ERROR.path(), player, plugin, placeholders);
            return true;
        }

        if (!PasswordSecurity.matchesLengthRequirement(password, plugin, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_DECLINE_ON_COMMON.path(), plugin)) {
                if (PasswordSecurity.isCommon(password, plugin, player.getName())) {
                    PlayerMessages.player.sendMessage(MessageKeys.REGISTER_ERROR_PASSWORD_TOO_SIMPLE.path(), player, plugin);
                    return;
                }
            }

            if (database.isPlayerPresentInDB(player.getName())) {
                PlayerMessages.player.sendMessage(MessageKeys.REGISTER_ERROR_ALREADY_REGISTERED.path(), player, plugin);
                return;
            }


            try {
                database.insertPlayer(player.getName(), password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            PlayerMessages.player.sendMessage(MessageKeys.REGISTER_SUCCESS.path(), player, plugin);

            PlayerStatus.setPlayerAsLogged(player, plugin, false, false);
        });


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<confirmPassword>");
            return list;
        }

        return null;
    }
}
