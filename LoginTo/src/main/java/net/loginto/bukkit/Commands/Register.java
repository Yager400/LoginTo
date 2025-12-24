/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.bukkit.Configuration.Messages.*;
import static net.loginto.bukkit.Configuration.Config.*;



import static net.loginto.bukkit.Configuration.LoggedPlayers.*;

import static net.loginto.bukkit.Configuration.SetPlayerStatus.*;

import java.util.ArrayList;
import java.util.List;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.JSON.JsonMenager;

public class Register implements CommandExecutor  {



    private final JavaPlugin plugin;

    private final DataBase database;

    public Register(JavaPlugin plugin, DataBase database) {
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
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }


        

        String password = args[0];

        String repetePassword = args[1];

        if (args.length != 2) {
            sender.sendMessage(getMessage("register.register_error", plugin));
            return true;
        }

        if (!password.equals(repetePassword)) {
            sender.sendMessage(getMessage("register.password_mismatch", plugin));
            return true;
        }




        if (isFeatureEnabled("password-security.required_character", plugin)) {

            final List<String> ReqChar = new ArrayList<>();

            for (char c : getStringFromConfig("password-security.characters_needed", plugin).toCharArray()) {
                ReqChar.add(String.valueOf(c));
            }

            for (char c : password.toCharArray()) {
                for (String s : ReqChar) {
                    if (String.valueOf(c).equals(s)) {
                        ReqChar.remove(s);
                    }
                }
            }

            Boolean missingChar = false;

            for (@SuppressWarnings("unused") String i : ReqChar) {
                missingChar = true;
                break;
            }

            if (!missingChar) {
                sender.sendMessage(getMessage("errors.register_character_error", plugin) + getStringFromConfig("password-security.characters_needed", plugin));
                return true;
            }
        }


        
        //Using JSON
        if (database == null) {

            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

            if (file.getString(player.getName() + ".password") != null) {
                sender.sendMessage(getMessage("errors.already_registered", plugin));
                return true;
            }

            if (!file.exists()) {
                sender.sendMessage(getMessage("errors.unexpected_error", plugin));
                return true;
            }

            file.set(player.getName() + ".password", password);
            file.save();
        }

        //Using DB
        else  {
            try {
                if (database.isPlayerPresentInDB(player)) {
                    sender.sendMessage(getMessage("errors.already_registered", plugin));
                    return true;
                }


            
                database.insertPlayer(player, password);
            } catch (Exception e) {}
        }

        addPlayer(player);

        sender.sendMessage(getMessage("register.register_success", plugin));
        unlockPlayer(player);

        return true;
    }
}
