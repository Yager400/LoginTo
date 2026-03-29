/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class LoginToFiles {
    
    public static class Messages {
        public static String getMessage(String path, Player player, Plugin plugin) {
            String text = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")).getString(path);

            if (text == null || text.isEmpty()) return "No message found for: " + path;

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
            } 

            return text;
        }
    }
    public static class Config {

        //YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml")).get(path);

        public static Object get(String path, Plugin plugin) {
            return plugin.getConfig().get(path, null);
        }

        public static Boolean isFeatureEnabled(String path, Plugin plugin) {
            return plugin.getConfig().getBoolean(path, false);
        }

        public static int getInt(String path, Plugin plugin) {
            return plugin.getConfig().getInt(path, 0);
        }
        
        public static String getString(String path, Plugin plugin) {
            return plugin.getConfig().getString(path, null);
        }

        public static Double getDouble(String path, Plugin plugin) {
            return plugin.getConfig().getDouble(path, 0);
        }

        public static List<?> getList(String path, Plugin plugin) {
            return plugin.getConfig().getList(path, null);
        }
    }

    public static void saveFiles(Plugin plugin) {
        
        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            plugin.saveResource("config.yml", false);
        }

        File messages = new File(plugin.getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        File oldPosition = new File(plugin.getDataFolder(), "oldPosition.json");
        if (!oldPosition.exists()) {
            try {
                oldPosition.createNewFile();

                Files.write(oldPosition.toPath(), "{}".getBytes(), StandardOpenOption.WRITE);

            } catch (IOException e) { e.printStackTrace(); }
        }

    }


}
