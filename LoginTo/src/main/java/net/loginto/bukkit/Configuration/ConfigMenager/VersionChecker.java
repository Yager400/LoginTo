/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration.ConfigMenager;


import static net.loginto.bukkit.Configuration.ConfigMenager.FileContent.getDefaultConfigFileContent;
import static net.loginto.bukkit.Configuration.ConfigMenager.FileContent.getDefaultMessageFileContent;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class VersionChecker {

    public static String ConfigVersion = "1.4";
    //TODO
    public static String MessageVersion = "1.2";

    public static void checkFilesVersion(Plugin plugin) {

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        File messageFile = new File(plugin.getDataFolder(), "messages.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        YamlConfiguration message = YamlConfiguration.loadConfiguration(messageFile);

        String actualConfigVersion = config.getString("ConfigVersion", "0");
        String actualMessageVersion = message.getString("MessageVersion", "0");

        if (!actualConfigVersion.equals(ConfigVersion)) {
            addNewConfig(configFile, config, plugin);
        }

        if (!actualMessageVersion.equals(MessageVersion)) {
            addNewMessage(messageFile, message, plugin);
        }
    }

    public static void addNewConfig(File configFile, YamlConfiguration userConfig, Plugin plugin) {

        String defaultContent = getDefaultConfigFileContent(plugin);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new StringReader(defaultContent));

        mergeConfigs(userConfig, defaultConfig);

        userConfig.set("ConfigVersion", ConfigVersion);

        try {
            userConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addNewMessage(File messageFile, YamlConfiguration userMessage, Plugin plugin) {

        String defaultContent = getDefaultMessageFileContent(plugin);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new StringReader(defaultContent));

        mergeConfigs(userMessage, defaultConfig);

        userMessage.set("MessageVersion", MessageVersion);

        try {
            userMessage.save(messageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void mergeConfigs(YamlConfiguration target, YamlConfiguration defaults) {
        for (String key : defaults.getKeys(true)) {
            if (!target.contains(key)) {
                target.set(key, defaults.get(key));
            }
        }
    }

}