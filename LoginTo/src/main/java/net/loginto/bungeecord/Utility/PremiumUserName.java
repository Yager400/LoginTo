/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.loginto.bungeecord.Database.SQLite;

public class PremiumUserName {
    
    public static boolean isUserNamePremium(String username, SQLite sqlite) {
        String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + username;

        if (sqlite.isPresent(username)) {
            return sqlite.getPremium(username);
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

                sqlite.add(username, true);
                return true;
            } else if (responseCode == 204 || responseCode == 404) {
                sqlite.add(username, false);
                return false;
            } else {
                sqlite.add(username, false);
                return false;
            }

        } catch (Exception e) {
            System.out.println("[loginto] Can't connect to api.mojang.com, the player with the name " + username + " got added as cracked");
            sqlite.add(username, false);
            return false;
        }
        
    }
}
