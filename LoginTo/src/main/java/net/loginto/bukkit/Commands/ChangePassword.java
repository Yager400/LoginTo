/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import static net.loginto.bukkit.Configuration.Config.isFeatureEnabled;
import static net.loginto.bukkit.Configuration.Messages.getMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.JSON.JsonMenager;

public class ChangePassword implements CommandExecutor  {



    private final JavaPlugin plugin;

    private final DataBase database;

    public ChangePassword(JavaPlugin plugin, DataBase database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        if (!sender.hasPermission("loginto.changepassword")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(getMessage("changepassword.correct_use_of_changepassword", plugin));
            return true;
        }

        String old_password = args[0];
        String new_password = args[1];

        
        //Using JSON
        if (database == null) {
            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

            if (!old_password.equals(file.getString(sender.getName() + ".password"))) {
                sender.sendMessage(getMessage("changepassword.old_password_wrong", plugin));
                return true;
            }

            file.set(sender.getName() + ".password", new_password);
            file.save();

        } 
        
        //Using DB
        else {
            try {
                if (!database.isPasswordCorrect((Player)sender, old_password)) {
                    sender.sendMessage(getMessage("changepassword.old_password_wrong", plugin));
                    return true;
                }

                database.changePlayerPassword((Player)sender, old_password, new_password);


            } catch (Exception e) {}
        }

        

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (isFeatureEnabled("kick-rules.kick_on_password_change", plugin)) {
                player.kickPlayer(getMessage("changepassword.change_psw_success_disconnected", plugin));
            } else {
                sender.sendMessage(getMessage("changepassword.change_psw_success", plugin));
            }
        }


        return true;
    }
}
