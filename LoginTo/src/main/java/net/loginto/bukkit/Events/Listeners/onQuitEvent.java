/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.PlayerUtils.Positions;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Utils.LoginToFiles;

public class onQuitEvent implements Listener {

    private final Plugin plugin;

    public onQuitEvent(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        
        if (Sessions.isPlayerLogged(event.getPlayer())) {
            Sessions.removePlayer(event.getPlayer());;
        }
        Tries.resetTries(event.getPlayer());

        if ((Boolean) LoginToFiles.Config.get("spawn-settings.teleport-on-join", plugin)) {
            Player player = event.getPlayer();
            Location playerLocation = player.getLocation();

            Positions.setPlayerPosition(
                player,
                playerLocation.getWorld(),
                playerLocation.getX(),
                playerLocation.getY(),
                playerLocation.getZ(),
                plugin
            );

            World world = Bukkit.getWorld((String) LoginToFiles.Config.get("spawn-settings.target-dimension", plugin));
            double x = (int) LoginToFiles.Config.get("spawn-settings.spawn-coordinates.x", plugin);
            double y = (int) LoginToFiles.Config.get("spawn-settings.spawn-coordinates.y", plugin);
            double z = (int) LoginToFiles.Config.get("spawn-settings.spawn-coordinates.z", plugin);

            Location location = new Location(world, x, y, z);

            player.teleport(location);
        }
    }
}
