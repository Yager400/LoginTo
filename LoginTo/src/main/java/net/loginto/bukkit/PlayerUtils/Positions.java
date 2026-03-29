/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Utils.JsonManager;
import net.loginto.bukkit.Utils.LoginToFiles;

public class Positions {
    
    public static void setPlayerPosition(Player player, World world, double x, double y, double z, Plugin plugin) {

        JsonManager oldPos = new JsonManager(plugin.getDataFolder(), "oldPosition.json");

        if (!LoginToFiles.Config.isFeatureEnabled("spawn-settings.teleport-on-join", plugin)) return;

        if (oldPos.getString(player.getName() + ".world") != null) return;

        oldPos.set(player.getName() + ".world", world.getName());
        oldPos.set(player.getName() + ".x", x);
        oldPos.set(player.getName() + ".y", y);
        oldPos.set(player.getName() + ".z", z);

        oldPos.save();

    }

    public static void teleportPlayerToTheOldPos(Player player, Plugin plugin) {

        JsonManager oldPos = new JsonManager(plugin.getDataFolder(), "oldPosition.json");

        
        if (!LoginToFiles.Config.isFeatureEnabled("spawn-settings.teleport-on-join", plugin)) return;
        if (!LoginToFiles.Config.isFeatureEnabled("spawn-settings.restore-previous-location", plugin)) return;
        
        if (oldPos.getString(player.getName() + ".world") == null) return;

        Location loc = new Location(
            Bukkit.getWorld(oldPos.getString(player.getName() + ".world")),
            oldPos.getInt(player.getName() + ".x"),
            oldPos.getInt(player.getName() + ".y"),
            oldPos.getInt(player.getName() + ".z")
        );

        
        player.teleport(loc);

        oldPos.remove(player.getName());

        oldPos.save();
        
    }
}
