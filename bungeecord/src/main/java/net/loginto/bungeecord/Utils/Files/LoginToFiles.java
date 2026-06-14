/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utils.Files;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class LoginToFiles {

    public static final URI rockyouURL = URI.create("https://weakpass.com/download/90/rockyou.txt.gz");

    public static class Messages {
        public static String getMessageLegacyComponent(String path) {
            return minimessageLegacyFormat(MiniMessage.miniMessage().deserialize(getMessageString(path)));
        }
        public static String getMessageString(String path) {
            return YamlRead(path, "messages.yml").toString();
        }
        public static String getMessageLegacyComponent(String path, HashMap<String, String> placeholders) {
            String text = getMessageString(path);

            for (String s : placeholders.keySet()) {
                text = text.replace(s, placeholders.get(s));
            }

            return minimessageLegacyFormat(MiniMessage.miniMessage().deserialize(text));
        }
        public static String getMessageLegacyComponent(String path, String end) {
            String text = getMessageString(path) + end;
            return minimessageLegacyFormat(MiniMessage.miniMessage().deserialize(text));
        }

        public static String minimessageLegacyFormat(Component text) {
            return LegacyComponentSerializer.legacySection().serialize(text);
        }
    }
    public static class Config {
        public static String getString(String path) {
            return YamlRead(path, "config.yml").toString();
        }
        public static int getInt(String path) {
            return Integer.parseInt(YamlRead(path, "config.yml").toString());
        }
        public static boolean isFeatureEnabled(String path) {
            return Boolean.parseBoolean(YamlRead(path, "config.yml").toString());
        }
        public static Object getObject(String path) {
            return YamlRead(path, "config.yml");
        }
        public static List<?> getList(String path) {
            return (List<?>) YamlRead(path, "config.yml");
        }
    }

    protected static Object YamlRead(String key, String fileName) {
        try (InputStream in = Files.newInputStream(Paths.get("plugins/loginto/" + fileName))) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.loadAs(in, Map.class);
            String[] parts = key.split("\\.");
            Object current = data;
            for (String part : parts) {
                if (!(current instanceof Map)) {
                    throw new RuntimeException("Key '" + key + "' is invalid, expected a nested map at '" + part + "'");
                }
                current = ((Map<String, Object>) current).get(part);
                if (current == null) {
                    if (key.equals("config-ver")) return "1.0";
                    throw new RuntimeException("Key '" + key + "' not found in " + fileName);
                }
            }
            return current;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Error reading " + fileName + ", make sure that the file exists and is not corrupted, more info about the error: ", e);
        }
    }

    public static void saveFiles(Plugin plugin) {
        File configFile = new File("plugins/loginto/config.yml");

        final String CONFIG_VER = "1.0";

        if (configFile.exists() && !Config.getString(ConfigKeys.CONFIG_VERSION.path()).equals(CONFIG_VER)) {
            configFile.renameTo(new File("plugins/loginto/config.yml.old"));
        }

        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        Path config = Paths.get("plugins", "loginto").resolve("config.yml");
        if (!configFile.exists()) {
            try {
                try (InputStream configIN = plugin.getClass().getClassLoader().getResourceAsStream("proxy/config.yml")) {
                    if (configIN == null) throw new IllegalStateException("config.yml not found in the jar file");
                    Files.copy(configIN, config);
                }
            } catch (IOException ignored) {}
        }

        File messageFile = new File("plugins/loginto/messages.yml");

        final String MESSAGE_VER = "1.0";

        if (messageFile.exists() && !Messages.getMessageString(MessageKeys.MESSAGE_VERSION.path()).equals(MESSAGE_VER)) {
            messageFile.renameTo(new File("plugins/loginto/messages.yml.old"));
        }

        if (!messageFile.getParentFile().exists()) {
            messageFile.getParentFile().mkdirs();
        }

        Path message = Paths.get("plugins", "loginto").resolve("messages.yml");
        if (!messageFile.exists()) {
            try {
                try (InputStream messageIn = plugin.getClass().getClassLoader().getResourceAsStream("proxy/messages.yml")) {
                    if (messageIn == null) throw new IllegalStateException("messages.yml not found in the jar file");
                    Files.copy(messageIn, message);
                }
            } catch (IOException ignored) {}
        }
    }

    public static void downloadRockYou(Logger logger) {
        CompletableFuture.runAsync(() -> {
            if (Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path())) {
                File txtFile = new File("plugins/loginto/rockyou.txt");

                if (!txtFile.exists()) {
                    try (GZIPInputStream gzipIn = new GZIPInputStream(rockyouURL.toURL().openStream());
                         FileOutputStream fileOut = new FileOutputStream(txtFile)) {

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = gzipIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                    }
                }
            }
        });
    }
}
