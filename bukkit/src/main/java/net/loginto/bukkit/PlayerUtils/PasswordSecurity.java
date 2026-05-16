/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import net.loginto.bukkit.Utils.LoginToFiles;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class PasswordSecurity {

    public static URI rockyouURL = URI.create("https://weakpass.com/download/90/rockyou.txt.gz");

    public static boolean isCommon(String password, Plugin plugin, String playerName) {
        if (LoginToFiles.Config.isFeatureEnabled("password-requirements.banned-password.use-rockyou", plugin)) {
            File txtFile = new File(plugin.getDataFolder(), "rockyou.txt");

            if (!txtFile.exists()) {
                downloadRockYou(plugin, txtFile);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals(password)) {
                        return true;
                    }
                }

                return false;
            } catch (IOException e) {
                plugin.getLogger().severe(e.getMessage());
            }
        }



        List<String> list = (List<String>) LoginToFiles.Config.getList("password-requirements.banned-password.banned-password", plugin);
        for (String s : list) {
            if (s.equals(password)) {
                return true;
            }

            if (s.equalsIgnoreCase("%username%")) {
                if (playerName.equalsIgnoreCase(password)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void downloadRockYou(Plugin plugin, File file) {
        try (GZIPInputStream gzipIn = new GZIPInputStream(rockyouURL.toURL().openStream());
             FileOutputStream fileOut = new FileOutputStream(file)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = gzipIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
    }


    public static boolean matchesLengthRequirement(String password, Plugin plugin, Player player) {
        if (!LoginToFiles.Config.isFeatureEnabled("password-requirements.length-check.enabled", plugin)) {
            return true;
        }
        int min = LoginToFiles.Config.getInt("password-requirements.length-check.min-length", plugin);
        int max = LoginToFiles.Config.getInt("password-requirements.length-check.max-length", plugin);
        if (password.length() < min || password.length() > max) {
            player.sendMessage(LoginToFiles.Messages.getMessage("register.error.password-length", player, plugin)
                .replaceAll("%min_length%", String.valueOf(min))
                .replaceAll("%max_length%", String.valueOf(max))
            );
            return false;
        }
        return true;
    }

    public static boolean doesIncludeReqChars(String password, Plugin plugin) {

        if (!LoginToFiles.Config.isFeatureEnabled("password-requirements.require-special-chars", plugin)) {
            return true;
        }

        final List<String> ReqChar = new ArrayList<>();

        for (char c : (LoginToFiles.Config.getString("password-requirements.required-char-list", plugin)).toCharArray()) {
            ReqChar.add(String.valueOf(c));
        }

        for (String c : ReqChar) {
            if (!password.contains(c)) {
                return false;
            }
        }

        return true;
    }
}
