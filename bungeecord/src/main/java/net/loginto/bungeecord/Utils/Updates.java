/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Updates {

    public static void checkForUpdates(ProxyServer server, Logger logger, Plugin plugin) {
        server.getScheduler().schedule(plugin, () -> {
            try {
                URL url = new URL("https://raw.githubusercontent.com/Yager400/LoginTo/refs/heads/main/velocity/src/main/resources/velocity-plugin.json");

                try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String gitVersion = json.get("version").getAsString();

                    if (gitVersion == null) return;

                    String version = plugin.getDescription().getVersion();

                    if (!version.equals(gitVersion)) {
                        logger.info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                    }
                }

            } catch (Exception e) {
                logger.severe("Unable to check for updates!");
            }
        }, 1, TimeUnit.HOURS);
    }
}
