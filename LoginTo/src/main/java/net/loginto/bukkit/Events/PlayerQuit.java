/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Configuration.OldPlayerPosition;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;
import static net.loginto.bukkit.ExtraFeature.Tries.removePlayerTries;
import static net.loginto.bukkit.Configuration.Config.*;

public class PlayerQuit implements Listener {

    private final Plugin plugin;

    public PlayerQuit(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isPlayerLogged(event.getPlayer())) {
            removePlayer(event.getPlayer());
        }
        removePlayerTries(event.getPlayer(), plugin);

        if (isFeatureEnabled("world_settings.teleport_on_join_enabled", plugin)) {
            Player player = event.getPlayer();
            Location playerLocation = player.getLocation();

            OldPlayerPosition.setPlayerPosition(
                event.getPlayer(), 
                playerLocation.getWorld(), 
                playerLocation.getX(), 
                playerLocation.getY(), 
                playerLocation.getZ(), 
                plugin
            );

            World world = Bukkit.getWorld(getStringFromConfig("world_settings.teleport_dimension", plugin));
            double x = plugin.getConfig().getDouble("world_settings.teleport_coordinates.x");
            double y = plugin.getConfig().getDouble("world_settings.teleport_coordinates.y");
            double z = plugin.getConfig().getDouble("world_settings.teleport_coordinates.z");

            Location location = new Location(world, x, y, z);

            player.teleport(location);
        }
    };
}
