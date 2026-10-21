/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerStatus {

    public static void setPlayerAsLogged(Player player) {
        Sessions.addPlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        // Execute those later
        LoginTo.getFoliaLib().getScheduler().runLater(() -> {
            // Teleport the player to the last position
            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SPAWNSETTING_RESTOREPREVIOUSLOCATION)
                    && LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SPAWNSETTING_ENABLED)) {
                String position = LoginTo.getDatabase().getLastLocationCords(player.getUniqueId());
                if (position != null && !position.isEmpty()) {
                    String[] positionInformation = position.split(";"); // world;x;y;z
                    World world = Bukkit.getWorld(positionInformation[0]);
                    double x = Double.parseDouble(positionInformation[1]);
                    double y = Double.parseDouble(positionInformation[2]);
                    double z = Double.parseDouble(positionInformation[3]);
                    Location location = new Location(world, x, y, z);
                    LoginTo.getFoliaLib().getScheduler().teleportAsync(player, location);
                }
            }

            // Update the player's inventory
            player.updateInventory();

            // Remove the potion effect from the player
            LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask -> {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            });
        }, 5L); // Run this after 0.25 seconds so the player can be fully initialized

        // Update the player's name since it might be null
        LoginTo.getDatabase().updateUsername(player.getUniqueId(), player.getName());
    }

    public static void setPlayerAsNotLogged(Player player) {
        Sessions.removePlayer(player.getUniqueId());

        Tries.removePlayer(player.getUniqueId());

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        LoginTo.getFoliaLib().getScheduler().runAtEntity(player, wrappedTask -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        });

        // Teleport to the spawn
        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SPAWNSETTING_ENABLED)) {
            World world =   Bukkit.getWorld(LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_SPAWNSETTING_CORDS_WORLD));
            double x =      LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SPAWNSETTING_CORDS_X) + 0.5;
            double y =      LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SPAWNSETTING_CORDS_Y) + 0.5;
            double z =      LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SPAWNSETTING_CORDS_Z) + 0.5;

            LoginTo.getFoliaLib().getScheduler().runLater(() -> {
                LoginTo.getFoliaLib().getScheduler().teleportAsync(player, new Location(world, x, y, z));
            }, 1);
        }
    }

}
