/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Commands;

import net.loginto.bukkit.PlayerUtils.PlayerMessages;
import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Database.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Login implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final Database database;

    public Login(Plugin plugin, Database database) {
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


        if (!sender.hasPermission("loginto.login")) {
            PlayerMessages.player.sendMessage(MessageKeys.ERRORS_GENERAL_NO_PERMISSION.path(), player, plugin);
            return true;
        }


        if (args.length != 1) {
            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_ERROR_USAGE.path(), player, plugin);
            return true;
        }


        if (Sessions.isPlayerLogged(player)) {
            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_ERROR_ALREADY_LOGGED_IN.path(), player, plugin);
            return true;
        }


        String password = args[0];

        if (!database.isPlayerPresentInDB(player.getName())) {
            PlayerMessages.player.sendMessage(MessageKeys.LOGIN_ERROR_NOT_REGISTERED.path(), player, plugin);
            return true;
        }

        try {
            if (!database.isPasswordCorrect(player.getName(), password)) {
                PlayerMessages.player.sendMessage(MessageKeys.LOGIN_ERROR_WRONG_PASSWORD.path(), player, plugin);
                if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_INVALID_PASSWORD.path(), plugin)) {
                    Tries.addTry(player);
                    if (Tries.triesEnded(player, plugin)) {
                        PlayerMessages.player.kickPlayer(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_FAILED_LOGIN.path(), player, plugin);
                    }
                }
            } else {
                PlayerMessages.player.sendMessage(MessageKeys.LOGIN_SUCCESS.path(), player, plugin);
                PlayerStatus.setPlayerAsLogged(player, plugin, false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("<password>");
            return list;
        }

        return null;
    }
}
