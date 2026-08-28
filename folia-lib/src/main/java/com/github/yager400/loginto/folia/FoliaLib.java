/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.folia;

import org.bukkit.plugin.Plugin;

public class FoliaLib {

    protected static Plugin plugin;

    public static void init(Plugin plugin) {
        FoliaLib.plugin = plugin;
    }

    public static Schedule get() {
        try {
            // If this paper/folia class exists, use the folia api for the player
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            return new FoliaSchedule();
        } catch (Exception e) {
            // If that paper/folia class doesn't exists, use the legacy schedule (this will also be used for paper < 1.20.1 and spigot)
            return new LegacySchedule();
        }
    }

}
