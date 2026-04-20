/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import net.md_5.bungee.api.plugin.Plugin;

public class FileMGR {

    public static void createBungeeConfigFile(Plugin plugin) {
        File file = new File("plugins/loginto/config.yml");

        final String CONFIG_VER = "1.3";

        if (file.exists() && !YamlRead("config-ver").equals(CONFIG_VER)) {
            file.renameTo(new File("plugins/loginto/config.yml.old"));
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        Path config = Paths.get("plugins", "loginto").resolve("config.yml");
        if (!file.exists()) {
            try {
                try (InputStream configIN = plugin.getClass().getClassLoader().getResourceAsStream("proxy/config.yml")) {
                    if (configIN == null) throw new IllegalStateException("config.yml not found in the jar file");
                    Files.copy(configIN, config);
                }  
            } catch (IOException e) {}
        }

        File filejson = new File("plugins/loginto/antispam.json");
        try {
            if (!filejson.exists()) {
                filejson.createNewFile();
            }
            Files.write(filejson.toPath(), "{}".getBytes());
        } catch (Exception e) {}
       
    }

    
    @SuppressWarnings("unchecked")
    public static String YamlRead(String key) {
        try (InputStream in = Files.newInputStream(Paths.get("plugins/loginto/config.yml"))) {
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
                    throw new RuntimeException("Key '" + key + "' not found in config.yml");
                }
            }

            return current.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Error reading config.yml, make sure that the file exists and is not corrupted, more info about the error: ", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> YamlReadList(String key) {
        try (InputStream in = Files.newInputStream(Paths.get("plugins/loginto/config.yml"))) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.loadAs(in, Map.class);
            String[] parts = key.split("\\.");
            Object current = data;

            for (String part : parts) {
                if (!(current instanceof Map)) {
                    throw new RuntimeException("Key '" + key + "' is invalid at '" + part + "'");
                }
                current = ((Map<String, Object>) current).get(part);
                if (current == null) {
                    throw new RuntimeException("Key '" + key + "' not found in config.yml");
                }
            }

            if (current instanceof List) {
                List<?> rawList = (List<?>) current;
                List<String> result = new ArrayList<>();
                for (Object item : rawList) {
                    result.add(item != null ? item.toString() : "");
                }
                return result;
            }

            throw new RuntimeException("Key '" + key + "' is not a list");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading list from config.yml: ", e);
        }
    }


}
