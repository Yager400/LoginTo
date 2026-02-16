/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class Version {

    public static String ConfigVersion = "1.5";
    //TODO
    public static String MessageVersion = "1.3";

    public static void checkFilesVersion(Plugin plugin) {

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File messageFile = new File(plugin.getDataFolder(), "messages.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        YamlConfiguration message = YamlConfiguration.loadConfiguration(messageFile);

        String actualConfigVersion = config.getString("ConfigVersion", "0");
        String actualMessageVersion = message.getString("MessageVersion", "0");

        if (!actualConfigVersion.equals(ConfigVersion)) {
            if (!new File(plugin.getDataFolder(), "config.yml.old").exists()) {
                configFile.renameTo(new File(plugin.getDataFolder(), "config.yml.old"));
                plugin.saveDefaultConfig();
            } else {
                plugin.getLogger().severe("Can't update the config.yml file, make sure to delete the old message file 'config.yml.old'");
            }
        }

        if (!actualMessageVersion.equals(MessageVersion)) {
            if (!new File(plugin.getDataFolder(), "messages.yml.old").exists()) {
                messageFile.renameTo(new File(plugin.getDataFolder(), "messages.yml.old"));
                plugin.saveResource("messages.yml", false);
            } else {
                plugin.getLogger().severe("Can't update the message.yml file, make sure to delete the old message file 'message.yml.old'");
            }
        }
    }

}