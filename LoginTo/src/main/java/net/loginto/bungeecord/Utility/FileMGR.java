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

        final String CONFIG_VER = "1.1";

        if (file.exists() && !YamlRead("config-ver").equals(CONFIG_VER)) {
            file.renameTo(new File("plugins/loginto/config.yml.old"));
        }

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                Files.write(file.toPath(), (
                    "config-ver: '1.1' # Do not change this\n\n" +
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
                    "anti_join-spam:\n" +
                    "   # This is for blocking the connection that will join and leave in a short period of time\n" +
                    "   anti_join-spam: 'true'\n" +
                    "\n" +
                    "   # This is the cooldown in seconds, if a player enter the network too many times in this period, it will be kicked\n" +
                    "   # By default this is set to 15, witch is pretty low, this is because an ip is a personal information,\n    # and it can't be saved for a long time without permission, if you are creating a large network, set this to 30-50\n" +
                    "   cooldown: '15'\n" +
                    "\n" +
                    "   # This is how many times the player can connect/reconnect before getting kicked, the sweet spot for this setting is 3-6, or if you want you can set to 1/2 and the cooldown to 5/10\n" + 
                    "   max-connection: '3'\n" +
                    "\n"+
                    "   # This will be the timeout (in seconds) of the player ban when it joins too many times\n" +
                    "   ban-time: 300\n" +
                    "\n" +
                    "   # The message that the player will see if he will join too many times\n" +
                    "   ban-message: 'You joined too many times with this ip, wait a few minutes'\n" +
                    "").getBytes());
                    
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


}
