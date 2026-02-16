/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;


public class Config {


    public static Boolean isFeatureEnabled(String key, Plugin plugin) {
        File file;

    
        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) plugin.saveDefaultConfig();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getBoolean(key);
    }

    public static String getStringFromConfig(String key, Plugin plugin) {
        File file;

    
        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) plugin.saveDefaultConfig();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getString(key);
    }

    public static int getIntFromConfig(String key, Plugin plugin) {
        File file;

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) plugin.saveDefaultConfig();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getInt(key);
    }



    
}
