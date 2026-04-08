/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Update {
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
                plugin.getLogger().severe("Unable to check for updates!");
            }

        }, 0, 72000L);
    }
}
