/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.players;

import com.github.yager400.loginto.common.data.PremiumCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerProtocolUtils {

    private static final Map<UUID, AuthenticatedPlayer> authenticatedPlayerMap = new HashMap<>();

    public static void addAuthenticatedPlayer(UUID uuid, AuthenticatedPlayer authenticatedPlayer) {
        authenticatedPlayerMap.put(uuid, authenticatedPlayer);
    }

    /**
     * Get the authenticated player for the uuid and delete it
     * @param uuid Player's uuid
     * @return The authenticated player
     */
    public static AuthenticatedPlayer getAuthenticatedPlayer(UUID uuid) {
        AuthenticatedPlayer player = authenticatedPlayerMap.get(uuid);
        authenticatedPlayerMap.remove(uuid);
        return player;
    }

    /**
     * See if this username has a premium account
     * @param username username
     * @param cacheDB premium cache database instance
     * @return Request response code
     */
    public static int getMojangAccountType(String username, PremiumCache cacheDB) {
        if (cacheDB.isPremiumCached(username)) {
            if (cacheDB.isPremium(username)) {
                return 200;
            } else {
                return 404;
            }
        }

        String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(2000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                cacheDB.addPremiumCachedRecord(username, true);

                return responseCode;
            }
            else {
                cacheDB.addPremiumCachedRecord(username, false);
                return responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 404;
        }
    }

    public static UUID getUUIDFromName(String username, List<?> premiumAuthBypassList, PremiumCache cacheDB) {

        if (cacheDB.isUUIDFromNameCached(username)) {
            return cacheDB.getUUIDFromName(username);
        }

        if (premiumAuthBypassList != null) {
            for (Object name : premiumAuthBypassList) {
                if (name instanceof String) {
                    if (name.equals(username)) {
                        UUID uuid = generateUUIDFromUsername(username);
                        cacheDB.addUUIDFromNameCachedRecord(username, uuid);
                        return uuid;
                    }
                }
            }
        }

        String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(2000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                    String id = json.get("id").getAsString();

                    long mostSigBits = Long.parseUnsignedLong(id.substring(0, 16), 16);
                    long leastSigBits = Long.parseUnsignedLong(id.substring(16, 32), 16);

                    UUID uuid = new UUID(mostSigBits, leastSigBits);
                    cacheDB.addUUIDFromNameCachedRecord(username, uuid);
                    return uuid;
                }
            }
            else {
                UUID uuid = generateUUIDFromUsername(username);
                cacheDB.addUUIDFromNameCachedRecord(username, uuid);
                return uuid;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UUID generateUUIDFromUsername(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

}
