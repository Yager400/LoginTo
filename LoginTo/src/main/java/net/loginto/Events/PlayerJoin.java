package net.loginto.Events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.DataBases.DataBase;
import net.loginto.JSON.JsonMenager;

import static net.loginto.Configuration.LoggedPlayers.*;
import static net.loginto.Configuration.SetPlayerStatus.*;

import static net.loginto.Configuration.Messages.*;
import static net.loginto.Configuration.Config.*;

import static net.loginto.ExtraFeature.TeleportToALocation.*;

public class PlayerJoin implements Listener {

    private final JavaPlugin plugin;

    private final DataBase database;

    public PlayerJoin(JavaPlugin instance, DataBase dataBase) {
        this.plugin = instance;

        this.database = dataBase;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (isFeatureEnabled("world.enabled_world_location_teleport", plugin)) {
            TPPlayer(event.getPlayer(), plugin);
        }

        if (!isPlayerLogged(event.getPlayer())) {
            lockPlayer(event.getPlayer());
            

            Boolean isFirstTimeInTheServer;

            //Using JSON
            if (database == null) {
                JsonMenager data = new JsonMenager(plugin.getDataFolder(), "data.json");
                if (data.getString(event.getPlayer().getName() + ".password") == null) {
                    isFirstTimeInTheServer = true;
                } else {
                    isFirstTimeInTheServer = false;
                }
            }

            else {
                if (!database.isPlayerPresentInDB(event.getPlayer())) {
                     isFirstTimeInTheServer = true;
                } else {
                    isFirstTimeInTheServer = false;
                }
            }

            String watermark;

            if (isFeatureEnabled("support.loginto-watermark", plugin)) {
                watermark = " - Service offered by LoginTo on Modrinth";
            } else {
                watermark = "";
            }

            if (isFirstTimeInTheServer) {
                if (isFeatureEnabled("password-security.required_character", plugin)) {
                    event.getPlayer().sendMessage(getMessage("register.register_prompt_characters", plugin) + getStringFromConfig("password-security.characters_needed", plugin) + ChatColor.GRAY +  watermark);
                } else {
                    event.getPlayer().sendMessage(getMessage("register.register_prompt", plugin) + ChatColor.GRAY +  watermark);
                }
                
            } else {
                event.getPlayer().sendMessage(getMessage("login.login_prompt", plugin));
            }
        }
    }; 
}
