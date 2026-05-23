/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import org.bukkit.Bukkit;

public class PluginUtils {

    public static boolean isPaperMC() {
        try {
            Class.forName("io.papermc.paper.configuration.GlobalConfiguration", false, Bukkit.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig", false, Bukkit.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ignored) {
        }


        return false;
    }
}
