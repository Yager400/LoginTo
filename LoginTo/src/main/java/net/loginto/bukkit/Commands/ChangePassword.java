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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;

public class ChangePassword implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Database database;
        
    public ChangePassword(Plugin plugin, Database database) {
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
        

        if (!sender.hasPermission("loginto.changepassword")) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("changepassword.error.changepassword-usage", player, plugin));
            return true;
        }

        String oldPassword = args[0];
        String newPassword = args[1];
        
        if (LoginToFiles.Config.isFeatureEnabled("password-requirements.require-special-chars", plugin)) {

            final List<String> ReqChar = new ArrayList<>();

            for (char c : (LoginToFiles.Config.getString("password-requirements.required-char-list", plugin)).toCharArray()) {
                ReqChar.add(String.valueOf(c));
            }

            for (String c : ReqChar) {
                if (!newPassword.contains(c)) {
                    sender.sendMessage(
                        LoginToFiles.Messages.getMessage("register.error.register-character-error", player, plugin)
                        .replace(
                            "%characters%", 
                            LoginToFiles.Config.getString("password-requirements.required-char-list", plugin)
                        ));
                    return true;
                }
            }
            
        }

        if (LoginToFiles.Config.isFeatureEnabled("password-requirements.length-check.enabled", plugin)) {
            int min = LoginToFiles.Config.getInt("password-requirements.length-check.min-length", plugin);
            int max = LoginToFiles.Config.getInt("password-requirements.length-check.max-length", plugin);

            if (newPassword.length() < min || newPassword.length() > max) {

                player.sendMessage(LoginToFiles.Messages.getMessage("register.error.password-length", player, plugin)
                        .replaceAll("%min_length%", String.valueOf(min))
                        .replaceAll("%max_length%", String.valueOf(max))
                    );

                return true;
            }
        }

        try {
            if (!database.isPasswordCorrect(player, oldPassword)) {
                sender.sendMessage(LoginToFiles.Messages.getMessage("changepassword.error.old-password-wrong", player, plugin));
                return false;
            }

            database.changePlayerPassword(player, oldPassword, newPassword);
            sender.sendMessage(LoginToFiles.Messages.getMessage("changepassword.password-changed", player, plugin));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<oldPassword>");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            list.add("<newPassword>");
            return list;
        }

        return null;
    }
}
