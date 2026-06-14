/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Utils;

import net.loginto.common.Database.Cache.PremiumSQLiteCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PremiumUtils {

    public static boolean checkValidUsername(String playerName) {
        return playerName.matches("^[a-zA-Z0-9_]{3,16}$");
    }

    public static boolean isUserNamePremium(String username, PremiumSQLiteCache sqLiteCache) {
        String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;

        if (sqLiteCache.isPresent(username)) {
            return sqLiteCache.getPremium(username);
        }

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

                sqLiteCache.add(username, true);
                return true;
            } else if (responseCode == 204 || responseCode == 404) {
                sqLiteCache.add(username, false);
                return false;
            } else {
                sqLiteCache.add(username, false);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            sqLiteCache.add(username, false);
            return false;
        }

    }
}
