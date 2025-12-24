/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.bukkit.Configuration.Messages.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static net.loginto.bukkit.Configuration.Config.*;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.JSON.JsonMenager;

public class DelAcc implements CommandExecutor  {


    private final JavaPlugin plugin;
    
    private final DataBase database;

    public DelAcc(JavaPlugin plugin, DataBase database) {
        this.plugin = plugin;
        this.database = database;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        if (isFeatureEnabled("permissions.op_required_delacc", plugin)) {
            if (!sender.isOp()) {
                sender.sendMessage(getMessage("errors.no_permission", plugin));
                return true;
            }
        }

        if (!sender.hasPermission("loginto.delacc")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(getMessage("errors.player_doesnt_exist", plugin));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(getMessage("delacc.delacc_wrong_syntax", plugin));
            return true;
        }

        if (!args[1].equals("confirm")) {
            sender.sendMessage(getMessage("delacc.delacc_not_confirmed", plugin));
            return true;
        }
        

        //Using JSON
        if (database == null) {
            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

            file.remove(target.getName());
            file.save();
        } 
        
        //Using DB
        else {
            try {

                database.removePlayer(target.getPlayer());

            } catch (Exception e) {}
        }

        sendDeletePremiumOrCrackedPass(target.getPlayer(), plugin);
        
        if (target.isOnline()) {
            target.getPlayer().kickPlayer(getMessage("delacc.admin_deleted_account", plugin));
        }

        sender.sendMessage(target.getName() + "'s account deleted");

        return true;
    }

    public static void sendDeletePremiumOrCrackedPass(Player player, Plugin plugin) {


        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("RemovePlayersInfo");
            out.writeUTF(player.getName());
            out.writeUTF("true");
            
            player.sendPluginMessage(plugin, "loginto:authchannel", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
