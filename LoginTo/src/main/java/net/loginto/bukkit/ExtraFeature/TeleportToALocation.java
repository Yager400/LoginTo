/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
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

        World world = Bukkit.getWorld(getStringFromConfig("world.world", plugin));

        int x = getIntFromConfig("world.x", plugin);
        int y = getIntFromConfig("world.y", plugin);
        int z = getIntFromConfig("world.z", plugin);

        Location loc = new Location(world, x, y, z);
        player.teleport(loc);

    }
    
}
