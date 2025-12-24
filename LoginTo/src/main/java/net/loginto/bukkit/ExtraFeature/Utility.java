/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.ExtraFeature;

import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Utility {
    public static String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static void checkForUpdates(Plugin plugin) {
        // Check if there is an update every hour
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {

                URL url = new URL("https://raw.githubusercontent.com/Yager400/LoginTo/main/LoginTo/src/main/resources/plugin.yml");
                
                
                InputStreamReader reader = new InputStreamReader(url.openStream());
                YamlConfiguration gitConfig = YamlConfiguration.loadConfiguration(reader);

                String gitVersion = gitConfig.getString("version");
                String currentVersion = plugin.getDescription().getVersion();

                if (gitVersion == null) return;

                if (!currentVersion.equals(gitVersion)) {
                    plugin.getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                }

            } catch (Exception e) {
                
            }
        }, 0L, 72000L);
    }
}
