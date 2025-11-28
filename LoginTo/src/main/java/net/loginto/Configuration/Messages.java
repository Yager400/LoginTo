package net.loginto.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static net.loginto.Configuration.ConfigMenager.FileContent.getDefaultMessageFileContent;


public class Messages {

    public static String getMessage(String key, Plugin plugin) {
        File file;

        

        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            file.mkdir();
            try {
                Files.write(file.toPath(), getDefaultMessageFileContent(plugin).getBytes());
            } catch (IOException e) {
                
            }
        }

        YamlConfiguration message = YamlConfiguration.loadConfiguration(file);

        return message.getString(key);

    }
}