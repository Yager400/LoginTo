package net.loginto.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;

import static net.loginto.ExtraFeature.VelocityServer.*;
import static net.loginto.ExtraFeature.WebHooks.*;



import static net.loginto.Configuration.LoggedPlayers.*;

import static net.loginto.Configuration.SetPlayerStatus.*;

import java.util.ArrayList;
import java.util.List;

import net.loginto.JSON.JsonMenager;

public class Register implements CommandExecutor  {



    private final JavaPlugin plugin;

    public Register(JavaPlugin plugin) {
        this.plugin = plugin;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        JsonMenager file = new JsonMenager(plugin.getDataFolder(), "data.json");

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Not a player");
            return true;
        }

        

        if (!sender.hasPermission("loginto.register")) {
            sender.sendMessage(getMessage("errors.no_permission", plugin));
            return true;
        }


        if (file.getString(player.getName() + ".password") != null) {
            sender.sendMessage(getMessage("errors.already_registered", plugin));
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

            for (String i : ReqChar) {
                missingChar = true;
                break;
            }

            if (!missingChar) {
                sender.sendMessage(getMessage("errors.register_character_error", plugin) + getStringFromConfig("password-security.characters_needed", plugin));
                return true;
            }
        }


        

        if (!file.exists()) {
            sender.sendMessage(getMessage("errors.unexpected_error", plugin));
            return true;
        }

        file.set(player.getName() + ".password", password);
        file.save();

        addPlayer(player);

        if (isFeatureEnabled("proxy-integration.go_to_server", plugin)) {
            sendPlayerToServer(player, plugin);
        }

        sender.sendMessage(getMessage("register.register_success", plugin));
        unlockPlayer(player);

        return true;
    }
}
