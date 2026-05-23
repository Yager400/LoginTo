/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Files;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LoginToFiles {

    public static class Messages {

        private static final MiniMessage mm;

        //Get the method 'get' if the server includes minimessage, but it's a version that
         // doesn't have 'MiniMessage.miniMessage();'
        static {
            MiniMessage tempMM;
            try {
                tempMM = (MiniMessage) MiniMessage.class.getMethod("get").invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                tempMM = MiniMessage.miniMessage();
            }
            mm = tempMM;
        }

        public static String getMessage(String path, Player player, Plugin plugin) {
            String text = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")).getString(path);

            if (text == null || text.isEmpty()) {
                return mmLegacySerialized(mm.deserialize("<red>No message found for: " + path));
            }

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                return mmLegacySerialized(mm.deserialize(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text)));
            }

            Component component = mm.deserialize(text);

            return mmLegacySerialized(component);
        }

        public static String getMessage(String path, Plugin plugin) {
            String text = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")).getString(path);

            if (text == null || text.isEmpty()) {
                return mmLegacySerialized(mm.deserialize("<red>No message found for: " + path));
            }

            Component component = mm.deserialize(text);

            return mmLegacySerialized(component);
        }

        private static String mmLegacySerialized(Component component) {
            return LegacyComponentSerializer.legacySection().serialize(component);
        }

    }

    public static class Config {

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

    public static class Experimental {
        private static File file = null;

        public static Object getExperimentalObj(String path, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return null;
                }
            }
            return YamlConfiguration.loadConfiguration(file).get(path);
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (LoginToFiles.Config.isFeatureEnabled("plugin-utility.enable-experimental-features", plugin)) {
            File experimentalFile = new File(plugin.getDataFolder(), "experimental.yml");
            if (!experimentalFile.exists()) {
                plugin.saveResource("experimental.yml", false);
            }
        }

    }


}
