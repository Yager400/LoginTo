/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static net.loginto.bukkit.Configuration.ConfigMenager.FileContent.getDefaultConfigFileContent;


public class Config {


    public static Boolean isFeatureEnabled(String key, Plugin plugin) {
        File file;

        

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.mkdir();
            try {
                Files.write(file.toPath(), getDefaultConfigFileContent(plugin).getBytes());
            } catch (IOException e) {

            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getBoolean(key);
    }

    public static String getStringFromConfig(String key, Plugin plugin) {
        File file;

        

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.mkdir();
            try {
                Files.write(file.toPath(), getDefaultConfigFileContent(plugin).getBytes());
            } catch (IOException e) {
                
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getString(key);
    }

    public static int getIntFromConfig(String key, Plugin plugin) {
        File file;

        

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            file.mkdir();
            try {
                Files.write(file.toPath(), getDefaultConfigFileContent(plugin).getBytes());
            } catch (IOException e) {
                
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config.getInt(key);
    }



    
}
