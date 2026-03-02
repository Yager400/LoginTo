/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.ExtraFeature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Config.*;

public class TeleportToALocation {

    public static void TPPlayer(Player player, Plugin plugin) {

        World world = Bukkit.getWorld(getStringFromConfig("world_settings.teleport_dimension", plugin));

        int x = getIntFromConfig("world_settings.teleport_coordinates.x", plugin);
        int y = getIntFromConfig("world_settings.teleport_coordinates.y", plugin);
        int z = getIntFromConfig("world_settings.teleport_coordinates.z", plugin);

        Location loc = new Location(world, x, y, z);
        player.teleport(loc);

    }
    
}
