/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class WebHooks {

    public static void register(Plugin plugin, Player player) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_REGISTER_MESSAGE.path(), plugin)
                .replaceAll("%playerName%", player.getName());

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_REGISTER_WEBHOOK_URL.path(), plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("register webhook request error, check the url");
            }
        }

    }

    public static void login(Plugin plugin, Player player) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_LOGIN_MESSAGE.path(), plugin)
                .replaceAll("%playerName%", player.getName());

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_LOGIN_WEBHOOK_URL.path(), plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("login webhook request error, check the url");
            }
        }

    }

    public static void unregister(Plugin plugin, Player player, Player target) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_UNREGISTER_MESSAGE.path(), plugin)
                .replaceAll("%playerName%", player.getName())
                .replaceAll("%targetPlayer%", target.getName());

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_UNREGISTER_WEBHOOK_URL.path(), plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("delacc webhook request error, check the url");
            }
        }


    }

    public static void changepassword(Plugin plugin, Player player) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_PASSWORD_CHANGE_MESSAGE.path(), plugin)
                .replaceAll("%playerName%", player.getName());

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_PASSWORD_CHANGE_WEBHOOK_URL.path(), plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("changepassword webhook request error, check the url");
            }
        }

    }

    @SuppressWarnings("deprecation")
    private static Boolean webhook(String webhookUrl, String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(message.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}