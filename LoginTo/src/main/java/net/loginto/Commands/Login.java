package net.loginto.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.Configuration.LoggedPlayers.addPlayer;
import static net.loginto.Configuration.SetPlayerStatus.*;

import java.util.Map;

import net.loginto.DataBases.DataBase;
import net.loginto.JSON.JsonMenager;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;

import static net.loginto.ExtraFeature.VelocityServer.*;

import java.util.HashMap;

public class Login implements CommandExecutor {


    private final JavaPlugin plugin;

    private final DataBase database;

    public Login(JavaPlugin plugin, DataBase database) {
        this.plugin = plugin;

        this.database = database;
    }


    private Map<Player, Integer> PlayersTry = new HashMap<>();



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        

        if (!(sender instanceof Player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        Player player = (Player) sender;
        

        if (!sender.hasPermission("loginto.login")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }

        

        if (args.length != 1) {
            sender.sendMessage(getMessage("login.login_error", plugin));
            return true;
        }

        String password = args[0];

        //Using JSON
        if (database ==null) {
            JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");
            
            if (file.getString(player.getName() + ".password") == null) {
                sender.sendMessage(getMessage("errors.not_registered", plugin));
                return true;
            }
            
            

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
        } 

        // Using DB
        else {

        
            if (!database.isPlayerPresentInDB(player)) {
                sender.sendMessage(getMessage("errors.not_registered", plugin));
                return true;
            }

            try {
                if (!database.isPasswordCorrect(player, password)) {
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
            } catch (Exception e) {}
        }


        return true;
    }
    
}
