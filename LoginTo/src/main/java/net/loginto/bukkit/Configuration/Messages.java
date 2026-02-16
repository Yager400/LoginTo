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

public class Messages {

    public static String getMessage(String key, Plugin plugin) {
        File file;

        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) plugin.saveResource("messages.yml", false);

        YamlConfiguration message = YamlConfiguration.loadConfiguration(file);

        return message.getString(key);

    }
}