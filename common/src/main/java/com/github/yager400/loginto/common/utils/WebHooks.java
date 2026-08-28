/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WebHooks {

    private static String webhookUrl = null;

    public static void setWebHookUrl(String url) {
        webhookUrl = url;
    }

    private static void sendWebHook(String message) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;
        CompletableFuture.runAsync(() -> {
            try {
                URL webhookUrlConnection = new URL(webhookUrl);
                HttpURLConnection conn = (HttpURLConnection) webhookUrlConnection.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(message.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String formatInformation(String information) {
        return String.format("{\"content\": \"%s\"}", information);
    }

    public static void sendRegisterWebhook(String playerName, UUID playerUUID) {
        String info = formatInformation(String.format("The player %s (%s) registered themself.", playerName, playerUUID));
        sendWebHook(info);
    }

    public static void sendLoginWebhook(String playerName, UUID playerUUID) {
        String info = formatInformation(String.format("The player %s (%s) logged in.", playerName, playerUUID));
        sendWebHook(info);
    }

    public static void sendUnRegisterWebhook(String playerName, UUID playerUUID, String targetName, UUID targetUUID) {
        String info = formatInformation(String.format("The player %s (%s) unregistered the account of %s (%s).", playerName, playerUUID, targetName, targetUUID));
        sendWebHook(info);
    }

}
