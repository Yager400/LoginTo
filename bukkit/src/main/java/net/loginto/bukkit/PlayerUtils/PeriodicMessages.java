/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.net.URL;

public class PeriodicMessages {

    public static void otpPeriodicMessage(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().getName().contains("qrcode")) {
                    PlayerMessages.player.sendMessage(MessageKeys.OTP_WORLD_PERIODIC_MESSAGE.path(), p, plugin);
                }
            }
        }, 0, 1200);
    }

    public static void checkForUpdates(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {

                @SuppressWarnings("deprecation")
                URL url = new URL("https://raw.githubusercontent.com/Yager400/LoginTo/refs/heads/main/bukkit/src/main/resources/config.yml");


                InputStreamReader reader = new InputStreamReader(url.openStream());
                YamlConfiguration gitConfig = YamlConfiguration.loadConfiguration(reader);

                String gitVersion = gitConfig.getString("version");
                String currentVersion = plugin.getDescription().getVersion();

                if (gitVersion == null) {
                    return;
                }

                if (!currentVersion.equals(gitVersion)) {
                    plugin.getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Unable to check for updates!");
            }
        }, 0, 72000L);
    }
}
