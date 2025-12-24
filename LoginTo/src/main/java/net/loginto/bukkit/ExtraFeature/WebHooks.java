/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.ExtraFeature;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Config.*;

public class WebHooks {

    public static void send_register_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = getStringFromConfig("discord-webhook.register_webhook", plugin);

        if (webhookUrl != null) {
            String message = "{\"content\": \""+ infoToView +"\"}";
        
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("register webhook request error, check the url");
            }
        }
        
    }

    public static void send_login_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = getStringFromConfig("discord-webhook.login_webhook", plugin);

        if (webhookUrl != null) {
            String message = "{\"content\": \""+ infoToView +"\"}";
        
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("login webhook request error, check the url");
            }
        }
        
    }

    public static void send_delacc_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = getStringFromConfig("discord-webhook.delacc_webhook", plugin);

        if (webhookUrl != null) {
            String message = "{\"content\": \""+ infoToView +"\"}";
                
            Boolean success = send_webhook(webhookUrl, message);
            if (!success) {
                plugin.getLogger().warning("delacc webhook request error, check the url");
            }
        }

        
    }

    public static void send_changepassword_webhook(String infoToView, Plugin plugin) {
        String webhookUrl = getStringFromConfig("discord-webhook.changepassword_webhook", plugin);

        if (webhookUrl != null) {
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
