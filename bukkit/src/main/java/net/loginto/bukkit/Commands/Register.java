/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.PlayerUtils.PasswordSecurity;
import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

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
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("register.error.register-usage", player, plugin));
            return true;
        }

        String password = args[0];

        String repetePassword = args[1];

        if (!password.equals(repetePassword)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("register.error.password-mismatch", player, plugin));
            return true;
        }

        if (!PasswordSecurity.doesIncludeReqChars(password, plugin)) {
            sender.sendMessage(
                    LoginToFiles.Messages.getMessage("register.error.register-character-error", player, plugin)
                            .replace(
                                    "%characters%",
                                    LoginToFiles.Config.getString("password-requirements.required-char-list", plugin)
                            ));
            return true;
        }

        if (!PasswordSecurity.matchesLengthRequirement(password, plugin, player)) {
            // The message get send in the PasswordSecurity.doesMatchLength function
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (PasswordSecurity.isCommon(password, plugin, player.getName())) {
                player.sendMessage(LoginToFiles.Messages.getMessage("register.error.password-too-simple", player, plugin));
                return;
            }

            if (database.isPlayerPresentInDB(player.getName())) {
                sender.sendMessage(LoginToFiles.Messages.getMessage("register.error.already-registered", player, plugin));
                return;
            }


            try {
                database.insertPlayer(player.getName(), password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sender.sendMessage(LoginToFiles.Messages.getMessage("register.register-success", player, plugin));

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
