/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utils;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.ConfigKeys;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class WebHooks {

    public static void register(String player, Logger logger) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_REGISTER_MESSAGE.path())
                .replaceAll("%playerName%", player);

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_REGISTER_WEBHOOK_URL.path());

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                logger.severe("register webhook request error, check the url");
            }
        }

    }

    public static void login(String player, Logger logger) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_LOGIN_MESSAGE.path())
                .replaceAll("%playerName%", player);

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_LOGIN_WEBHOOK_URL.path());

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                logger.severe("login webhook request error, check the url");
            }
        }

    }

    public static void unregister(String player, String target, Logger logger) {
        String infoToView = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_UNREGISTER_MESSAGE.path())
                .replaceAll("%playerName%", player)
                .replaceAll("%targetPlayer%", target);

        String webhookUrl = LoginToFiles.Config.getString(ConfigKeys.INTEGRATIONS_DISCORD_UNREGISTER_WEBHOOK_URL.path());

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \"" + infoToView + "\"}";

            Boolean success = webhook(webhookUrl, message);
            if (!success) {
                logger.severe("delacc webhook request error, check the url");
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