package net.loginto.bukkit.Commands;

import static net.loginto.bukkit.Configuration.Config.isFeatureEnabled;
import static net.loginto.bukkit.Configuration.Messages.getMessage;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.PlayersLogger.*;

import java.util.List;

public class PlayersLog implements CommandExecutor {

    private final Plugin plugin;

    public PlayersLog(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("loginto.getlogs")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return false;
        }

        if (!isFeatureEnabled("logging.logging", plugin)) {
            sender.sendMessage(getMessage("errors.feature_not_enabled", plugin));
            return false;
        }

        if (args.length < 1 || args.length > 2) {
            return false;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        

        List<String> data;

        if (args.length != 2) {
            data = getLogs(target.getPlayer(), plugin, "n");
        } else {
            data = getLogs(target.getPlayer(), plugin, args[1]);
        }

        if (data.size() <= 0) {
            sender.sendMessage("No logs for " + args[0]);
        } else {
            sender.sendMessage("§l" + args[0] + "§r logs:\n-----\n");
            for (String i : data) {
                String format = i + "\n-----\n";
                sender.sendMessage(format);
            }
        }

        return true;
    }
}
