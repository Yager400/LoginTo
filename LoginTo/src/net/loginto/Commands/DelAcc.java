package net.loginto.Commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;


import net.loginto.JSON.JsonMenager;

public class DelAcc implements CommandExecutor  {


    private final JavaPlugin plugin;

    public DelAcc(JavaPlugin plugin) {
        this.plugin = plugin;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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
        

        JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

        file.remove(target.getName());
        file.save();

        if (target.isOnline()) {
            target.getPlayer().kickPlayer(getMessage("delacc.admin_deleted_account", plugin));
        }

        sender.sendMessage(target.getName() + "'s account deleted");


        return true;
    }
}
