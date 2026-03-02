/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


import java.io.File;

public class Messages {

    public static String getMessage(String key, Plugin plugin) {
        File file;

        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) plugin.saveResource("messages.yml", false);

        YamlConfiguration message = YamlConfiguration.loadConfiguration(file);

        return message.getString(key);

    }

    public static String PAPIFormat(Player target, String text) {

        if (text == null || text.isEmpty()) return "No message found, check the messages.yml file";

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(target, text);
        } 

        return text;

    }
}