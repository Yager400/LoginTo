/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.bukkit.ExtraFeature.Tries.*;
import static net.loginto.bukkit.Configuration.LoggedPlayers.addPlayer;
import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;
import static net.loginto.bukkit.Configuration.SetPlayerStatus.*;
import static net.loginto.bukkit.Configuration.PlayersLogger.*;

import net.loginto.bukkit.Configuration.Messages;
import net.loginto.bukkit.Configuration.OldPlayerPosition;
import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.ExtraFeature.Utility;
import net.loginto.bukkit.ExtraFeature.WebHooks;
import net.loginto.bukkit.JSON.JsonMenager;
import net.loginto.bukkit.Premium.Check;


public class Login implements CommandExecutor {


    private final JavaPlugin plugin;

    private final DataBase database;

    public Login(JavaPlugin plugin, DataBase database) {
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
            sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.no_permission", plugin)));
            return true;
        }

        

        if (args.length != 1) {
            sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("login.login_error", plugin)));
            return true;
        }


        if (isPlayerLogged(player)) {
            sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.already_logged_in", plugin)));
            return true;
        }


        String password = args[0];

        //Using JSON
        if (database ==null) {
            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");
            
            if (file.getString(player.getName() + ".password") == null) {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.not_registered", plugin)));
                return true;
            }
            
            

            if (!file.exists()) {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.unexpected_error", plugin)));
                return true;
            }

            if (!file.getString(player.getName() + ".password").equals(password)) {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("login.wrong_password", plugin)));
                incrementTries(player, plugin);
            } else {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("login.login_success", plugin)));
                unlockPlayer(player);
                addPlayer(player);
                removePlayerTries(player, plugin);
                logPlayer(player, plugin, false);
                OldPlayerPosition.teleportPlayerToTheOldPos(player, plugin);
                WebHooks.send_register_webhook(Utility.getFormattedWebhookMessage("login", player, null, plugin), plugin);
                Check.SetLoggedPlayer(plugin, player);
                player.updateInventory();
            }
        } 

        // Using DB
        else {

        
            if (!database.isPlayerPresentInDB(player)) {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.not_registered", plugin)));
                return true;
            }

            try {
                if (!database.isPasswordCorrect(player, password)) {
                    sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("login.wrong_password", plugin)));
                    incrementTries(player, plugin);
                } else {
                    sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("login.login_success", plugin)));
                    unlockPlayer(player);
                    addPlayer(player);
                    removePlayerTries(player, plugin);
                    logPlayer(player, plugin, false);
                    OldPlayerPosition.teleportPlayerToTheOldPos(player, plugin);
                    WebHooks.send_register_webhook(Utility.getFormattedWebhookMessage("login", player, null, plugin), plugin);
                    Check.SetLoggedPlayer(plugin, player);
                    player.updateInventory();
                }
            } catch (Exception e) {}
        }


        return true;
    }
    
}
