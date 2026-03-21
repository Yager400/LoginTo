/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.Plugin;


public class WebHooks {

    public static void send_register_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = (String) LoginToFiles.Config.get("integrations.discord.register-webhook-url", plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \""+ infoToView +"\"}";
        
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("register webhook request error, check the url");
            }
        }
        
    }

    public static void send_login_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = (String) LoginToFiles.Config.get("integrations.discord.login-webhook-url", plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \""+ infoToView +"\"}";
        
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("login webhook request error, check the url");
            }
        }
        
    }

    public static void send_delacc_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = (String) LoginToFiles.Config.get("integrations.discord.delete-account-webhook-url", plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \""+ infoToView +"\"}";
                
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("delacc webhook request error, check the url");
            }
        }

        
    }

    public static void send_changepassword_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = (String) LoginToFiles.Config.get("integrations.discord.password-change-webhook-url", plugin);

        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            String message = "{\"content\": \""+ infoToView +"\"}";

            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("changepassword webhook request error, check the url");
            }
        }
        
    }

    public static Boolean send_webhook(String webhookUrl, String message) {
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