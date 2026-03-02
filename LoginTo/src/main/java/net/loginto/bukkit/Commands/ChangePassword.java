/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import static net.loginto.bukkit.Configuration.Config.*;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.bukkit.Configuration.Messages;
import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.ExtraFeature.Utility;
import net.loginto.bukkit.ExtraFeature.WebHooks;
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

        Player player = (Player) sender;

        if (!sender.hasPermission("loginto.changepassword")) {
            sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("errors.no_permission", plugin)));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("changepassword.correct_use_of_changepassword", plugin)));
            return true;
        }

        String old_password = args[0];
        String new_password = args[1];



        if (isFeatureEnabled("password-security.required_character", plugin)) {

            final List<String> ReqChar = new ArrayList<>();

            for (char c : getStringFromConfig("password-security.characters_needed", plugin).toCharArray()) {
                ReqChar.add(String.valueOf(c));
            }

            for (String c : ReqChar) {
                if (!new_password.contains(c)) {
                    sender.sendMessage(
                        Messages.PAPIFormat(player, Messages.getMessage("errors.register_character_error", plugin))
                        .replace(
                            "%characters%", 
                            getStringFromConfig("password-security.characters_needed", plugin)
                        ));
                    return true;
                }
            }
            
        }

        if (isFeatureEnabled("password-security.password_length.enabled", plugin)) {
            int min = getIntFromConfig("password-security.password_length.min_length", plugin);
            int max = getIntFromConfig("password-security.password_length.max_length", plugin);

            if (new_password.length() < min || new_password.length() > max) {

                player.sendMessage(Messages.PAPIFormat(
                    (Player) sender, 
                    Messages.getMessage("errors.password_length", plugin)
                        .replaceAll("%min_length%", String.valueOf(min))
                        .replaceAll("%max_length%", String.valueOf(max))
                ));

                return true;
            }
        }

        
        //Using JSON
        if (database == null) {
            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

            if (!old_password.equals(file.getString(sender.getName() + ".password"))) {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("changepassword.old_password_wrong", plugin)));
                return true;
            }

            file.set(sender.getName() + ".password", new_password);
            file.save();

        } 
        
        //Using DB
        else {
            try {
                if (!database.isPasswordCorrect((Player)sender, old_password)) {
                    sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("changepassword.old_password_wrong", plugin)));
                    return true;
                }

                database.changePlayerPassword((Player)sender, old_password, new_password);


            } catch (Exception e) {}
        }

        

        if (sender instanceof Player) {

            if (isFeatureEnabled("kick-rules.kick_on_password_change", plugin)) {
                player.kickPlayer(Messages.PAPIFormat(player, Messages.getMessage("changepassword.change_psw_success_disconnected", plugin)));
            } else {
                sender.sendMessage(Messages.PAPIFormat(player, Messages.getMessage("changepassword.change_psw_success", plugin)));
            }

            WebHooks.send_changepassword_webhook(Utility.getFormattedWebhookMessage("changepassword", player, null, plugin), plugin);
        }


        return true;
    }
}
