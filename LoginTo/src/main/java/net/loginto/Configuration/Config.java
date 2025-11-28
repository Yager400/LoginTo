package net.loginto.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static net.loginto.Configuration.ConfigMenager.FileContent.getDefaultConfigFileContent;


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
