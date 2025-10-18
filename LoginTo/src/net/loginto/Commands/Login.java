package net.loginto.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.Configuration.LoggedPlayers.addPlayer;
import static net.loginto.Configuration.SetPlayerStatus.*;

import java.util.Map;

import net.loginto.JSON.JsonMenager;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;

import static net.loginto.ExtraFeature.VelocityServer.*;
import static net.loginto.ExtraFeature.WebHooks.*;

import java.io.IOError;
import java.util.HashMap;


public class Login implements CommandExecutor {


    private final JavaPlugin plugin;

    public Login(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    private Map<Player, Integer> PlayersTry = new HashMap<>();



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        if (!sender.hasPermission("loginto.login")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }

        if (file.getString(player.getName() + ".password") == null) {
            sender.sendMessage(getMessage("errors.not_registered", plugin));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(getMessage("login.login_error", plugin));
            return true;
        }

        /*
         * doesnt work
         
        try {
            if (PlayersTry.get(player) >= getIntFromConfig("kick-rules.tries", plugin)) {
                player.getPlayer().kickPlayer(getMessage("login.message_limit_end", plugin));
                PlayersTry.remove(player);
                return true;
            }
        } catch(IOError e) {}
         */

        String password = args[0];

        

        

        if (!file.exists()) {
            sender.sendMessage(getMessage("errors.unexpected_error", plugin));
            return true;
        }



        if (!file.getString(player.getName() + ".password").equals(password)) {
            sender.sendMessage(getMessage("login.wrong_password", plugin));
            int current = PlayersTry.getOrDefault(player, 1);
            PlayersTry.put(player, current + 1);
        } else {
            if (isFeatureEnabled("proxy-integration.go_to_server", plugin)) {
                sendPlayerToServer(player, plugin);
            }
            sender.sendMessage(getMessage("login.login_success", plugin));
            unlockPlayer(player);
            addPlayer(player);
        }


        return true;
    }
    
}
