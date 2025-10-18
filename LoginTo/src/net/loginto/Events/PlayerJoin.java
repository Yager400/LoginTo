package net.loginto.Events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.loginto.JSON.JsonMenager;

import static net.loginto.Configuration.LoggedPlayers.*;
import static net.loginto.Configuration.SetPlayerStatus.*;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;

import static net.loginto.ExtraFeature.TeleportToALocation.*;

public class PlayerJoin implements Listener {

    private final JavaPlugin plugin;

    public PlayerJoin(JavaPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (isFeatureEnabled("world.enabled_world_location_teleport", plugin)) {
            TPPlayer(event.getPlayer(), plugin);
        }

        if (!isPlayerLogged(event.getPlayer())) {
            lockPlayer(event.getPlayer());
            JsonMenager data = new JsonMenager(plugin.getDataFolder(), "data.json");

            Boolean isFirstTimeInTheServer;

            if (data.getString(event.getPlayer().getName() + ".password") == null) {
                isFirstTimeInTheServer = true;
            } else {
                isFirstTimeInTheServer = false;
            }

            if (isFirstTimeInTheServer) {
                if (isFeatureEnabled("password-security.required_character", plugin)) {
                    event.getPlayer().sendMessage(getMessage("register.register_prompt_characters", plugin) + getStringFromConfig("password-security.characters_needed", plugin) + ChatColor.GRAY +  " - Service offered by LoginTo on Modrinth");
                } else {
                    event.getPlayer().sendMessage(getMessage("register.register_prompt", plugin) + ChatColor.GRAY +  " - Service offered by LoginTo on Modrinth");
                }
                
            } else {
                event.getPlayer().sendMessage(getMessage("login.login_prompt", plugin));
            }
        }
    }; 
}
