/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class FileMGR {

    public static void createBungeeConfigFile() {
        File file = new File("plugins/loginto/config.yml");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                Files.write(file.toPath(), (
                    "# Hi, welcome to the LoginTo premium configuration, you will need to write here only a few information and follow the comments\n" +
                    "\n" +
                    "# Before starting please make sure that you server is not on a slow network, otherwise this feature probably will not work\n" +
                    "# It won't need to be a gigabit but that can make a request in under 2 second (a normal network should be good)\n" +
                    "# Also if your network will support bedrock players use a prefix that java names cannot contain, for example a dot (.) , but NOT use the underscore (_), letters, numbers or anything affected by SQL case sensitivity\n" +
                    "\n" +
                    "# Should LoginTo authenticate players with mojang? (if this option is false the proxy will be 100% offline, if true the classic authentication will be executed)\n" +
                    "loginto-premium-auth: 'true'\n" +
                    "\n" +
                    "# This plugin uses H2 database so no external database installation is required\n" +
                    "\n" +
                    "# This will be the database info for sharing data with the bukkit server\n" +
                    "database:\n" +
                    "\n" +
                    "  # The port of your database, the H2 default port is 9092\n" +
                    "  port: 9092\n" +
                    "\n" +
                    "\n" +
                    "# This is the user authentication variables\n" +
                    "# They will be used to validate an username and bedrock players\n" +
                    "user-auth:\n" +
                    "\n" +
                    "   # This will be the prefix for bedrock players\n" +
                    "   # WARN: The bedrock feature is nothing special, basicalle while validating the username we will check if the name start with the prefix\n" +
                    "   # Also as the warn on top, do not use java-accepted characters or sql sensitive characters\n" +
                    "   bedrock-prefix: '.'\n" +
                    "\n" +
                    "# This will be the section for the proxy messages\n" +
                    "# Some messages are client only, so you can't change that\n" +
                    "messages:\n" +
                    "\n" +
                    "   # This message will appear if the player joining has an invalid username\n" +
                    "   invalid-username: 'You got kicked because you username is invalid: %username%'\n" +
                    "\n" +
                    "# This is for letting LoginTo save the player sessions, this is usefull if a player will return to the lobby\n" +
                    "# The player session will be removed when he will disconnect from the proxy\n" +
                    "use-player-sessions: 'true'\n" +
                    "").getBytes());
                    
            } catch (IOException e) {}
        }
    }

    
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


}
