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
import net.loginto.bukkit.PlayerUtils.PasswordSecurity;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class LoginToFiles {

    public static final URI rockyouURL = URI.create("https://weakpass.com/download/90/rockyou.txt.gz");

    public static class Messages {

        private static final MiniMessage mm = MiniMessage.miniMessage();

        public static Component getMessage(String path, Player player, Plugin plugin, Map<String, String> placeholders) {
            String text = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")).getString(path);

            if (text == null || text.isEmpty()) {
                return mm.deserialize("<red>No message found for: " + path);
            }

            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    text = text.replace(entry.getKey(), entry.getValue());
                }
            }

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                return mm.deserialize(me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text));
            }

            return mm.deserialize(text);
        }

        public static Component getMessage(String path, Plugin plugin, Map<String, String> placeholders) {
            String text = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")).getString(path);

            if (text == null || text.isEmpty()) {
                return mm.deserialize("<red>No message found for: " + path);
            }

            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    text = text.replace(entry.getKey(), entry.getValue());
                }
            }

            return mm.deserialize(text);
        }

        public static void setMessageValue(String path, Object value, Plugin plugin) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
            config.set(path, value);
            try {
                config.save(new File(plugin.getDataFolder(), "messages.yml"));
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
            }
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

        public static int getIntOrDefault(String path, Plugin plugin, int defaultValue) {
            int i = getInt(path, plugin);
            if (i == 0) {
                return defaultValue;
            } else {
                return i;
            }
        }

        public static String getString(String path, Plugin plugin) {
            return plugin.getConfig().getString(path, null);
        }

        public static String getStringOrDefault(String path, Plugin plugin, String defaultValue) {
            String i = getString(path, plugin);
            if (i == null || i.isEmpty()) {
                return defaultValue;
            } else {
                return i;
            }
        }

        public static Double getDouble(String path, Plugin plugin) {
            return plugin.getConfig().getDouble(path, 0);
        }

        public static Double getDoubleOrDefault(String path, Plugin plugin, Double defaultValue) {
            Double i = getDouble(path, plugin);
            if (i == 0) {
                return defaultValue;
            } else {
                return i;
            }
        }

        public static List<?> getList(String path, Plugin plugin) {
            return plugin.getConfig().getList(path, null);
        }

        public static void setConfigValue(String path, Object value, Plugin plugin) {
            plugin.getConfig().set(path, value);
            try {
                plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
            }
        }
    }

    public static class Experimental {
        private static File file = null;

        public static void setExperimentalValue(String path, Object value, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return;
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set(path, value);
            try {
                config.save(new File(plugin.getDataFolder(), "experimental.yml"));
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
            }
        }

        public static Object getExperimentalObj(String path, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return null;
                }
            }
            return YamlConfiguration.loadConfiguration(file).get(path);
        }

        public static boolean getExperimentalBoolean(String path, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return false;
                }
            }
            return YamlConfiguration.loadConfiguration(file).getBoolean(path);
        }

        public static int getExperimentalInteger(String path, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return 0;
                }
            }
            return YamlConfiguration.loadConfiguration(file).getInt(path);
        }

        public static String getExperimentalString(String path, Plugin plugin) {
            if (file == null) {
                file = new File(plugin.getDataFolder(), "experimental.yml");
                if (!file.exists()) {
                    return null;
                }
            }
            return YamlConfiguration.loadConfiguration(file).getString(path);
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

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_USE_EXPERIMENTAL_FEATURES.path(), plugin)) {
            plugin.getLogger().info("Thank you for using experimental features, if you find any bug or want to suggest a feature, you can do that here: https://github.com/Yager400/LoginTo/issues\nRemember that those features are still in beta and they might contain bugs. ");
            File experimentalFile = new File(plugin.getDataFolder(), "experimental.yml");
            if (!experimentalFile.exists()) {
                plugin.saveResource("experimental.yml", false);
            }
        }
    }

    public static void downloadRockYou(Plugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path(), plugin)) {
                File txtFile = new File(plugin.getDataFolder(), "rockyou.txt");

                if (!txtFile.exists()) {
                    try (GZIPInputStream gzipIn = new GZIPInputStream(rockyouURL.toURL().openStream());
                         FileOutputStream fileOut = new FileOutputStream(txtFile)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = gzipIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        plugin.getLogger().severe(e.getMessage());
                    }
                }
            }
        });
    }
}
