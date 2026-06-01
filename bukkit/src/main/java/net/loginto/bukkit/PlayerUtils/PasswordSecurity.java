/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class PasswordSecurity {

    public static boolean isCommon(String password, Plugin plugin, String playerName) {
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path(), plugin)) {
            File txtFile = new File(plugin.getDataFolder(), "rockyou.txt");

            /*if (!txtFile.exists()) {
                downloadRockYou(plugin, txtFile);
            }*/

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

        List<?> listUnknownElements = LoginToFiles.Config.getList(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_LIST.path(), plugin);
        List<String> list = new ArrayList<>();

        for (Object i : listUnknownElements) {
            if (i instanceof String) {
                String s = (String) i;
                list.add(s);
            }
        }

        if (listUnknownElements.size() != list.size()) {
            plugin.getLogger().severe("Some banned password got excluded, make sure that all of them are strings");
        }

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

    public static boolean matchesLengthRequirement(String password, Plugin plugin, Player player) {
        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_ENABLED.path(), plugin)) {
            return true;
        }
        int min = LoginToFiles.Config.getInt(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_MIN_LENGTH.path(), plugin);
        int max = LoginToFiles.Config.getInt(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_MAX_LENGTH.path(), plugin);
        if (password.length() < min || password.length() > max) {
            player.sendMessage(LoginToFiles.Messages.getMessage(MessageKeys.REGISTER_ERROR_PASSWORD_LENGTH.path(), player, plugin)
                    .replaceAll("%min_length%", String.valueOf(min))
                    .replaceAll("%max_length%", String.valueOf(max))
            );
            return false;
        }
        return true;
    }

    public static boolean doesIncludeReqChars(String password, Plugin plugin) {

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS.path(), plugin)) {
            return true;
        }

        final List<String> ReqChar = new ArrayList<>();

        for (char c : (LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path(), plugin)).toCharArray()) {
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
