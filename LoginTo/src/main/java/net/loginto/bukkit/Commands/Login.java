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

import net.loginto.bukkit.PlayerUtils.PlayerStatus;
import net.loginto.bukkit.PlayerUtils.Positions;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.LoginToFiles;

public class login implements CommandExecutor, TabCompleter {
        
    private final Plugin plugin;
    private final Database database;

    public login(Plugin plugin, Database database) {
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
            sender.sendMessage(LoginToFiles.Messages.getMessage("errors.general.no-permission", player, plugin));
            return true;
        }

        

        if (args.length != 1) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("login.error.login-usage", player, plugin));
            return true;
        }


        if (Sessions.isPlayerLogged(player)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("login.error.already-logged-in", player, plugin));
            return true;
        }


        String password = args[0];

        if (!database.isPlayerPresentInDB(player)) {
            sender.sendMessage(LoginToFiles.Messages.getMessage("login.error.not-registered", player, plugin));
            return true;
        }

        try {
            if (!database.isPasswordCorrect(player, password)) {
                sender.sendMessage(LoginToFiles.Messages.getMessage("login.error.wrong-password", player, plugin));
                Tries.addTry(player);
                if (Tries.triesEnded(player, plugin)) {
                    player.kickPlayer(LoginToFiles.Messages.getMessage("errors.login-fail.onkick-for-failed-login", player, plugin));
                }
            } else {
                sender.sendMessage(LoginToFiles.Messages.getMessage("login.login-success", player, plugin));
                PlayerStatus.setPlayerAsLogged(player, plugin, false);
                if ((Boolean) LoginToFiles.Config.get("spawn-settings.teleport-on-join", plugin) && (Boolean) LoginToFiles.Config.get("spawn-settings.restore-previous-location", plugin)) {
                    Positions.teleportPlayerToTheOldPos(player, plugin);
                }
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
