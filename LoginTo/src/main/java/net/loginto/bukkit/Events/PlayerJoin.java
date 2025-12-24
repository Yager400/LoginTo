/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.JSON.JsonMenager;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;
import static net.loginto.bukkit.Configuration.SetPlayerStatus.*;
import static net.loginto.bukkit.ExtraFeature.Tries.*;
import static net.loginto.bukkit.Configuration.Messages.*;
import static net.loginto.bukkit.Configuration.Config.*;

import static net.loginto.bukkit.Premium.Check.CheckIfAPlayerCanAutoLogin;

import static net.loginto.bukkit.ExtraFeature.TeleportToALocation.*;

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

                if (isFeatureEnabled("premium.premium", plugin)) {
                    if (CheckIfAPlayerCanAutoLogin(event.getPlayer(), plugin)) {
                        addPlayer(event.getPlayer());
                        event.getPlayer().sendMessage(getMessage("login.login_success", plugin));
                        unlockPlayer(event.getPlayer());
                        return;
                    }
                }

                    
                event.getPlayer().sendMessage(getMessage("login.login_prompt", plugin));
                addPlayerTries(event.getPlayer(), plugin);
                    


                
            }

            if (isFeatureEnabled("kick-rules.kick_on_too_many_second_passed", plugin)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!isPlayerLogged(event.getPlayer())) {
                            event.getPlayer().kickPlayer(getMessage("errors.onkick_for_long_waiting", plugin));
                        }
                    }
                }.runTaskLater(plugin, getIntFromConfig("kick-rules.seconds_before_kick", plugin) * 20L);
            }
        }
    }; 
}
