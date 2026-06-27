/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.PlayerUtils;

import com.velocitypowered.api.proxy.Player;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PasswordSecurity {

    private static final String autoRegisterLower = "abcdefghijklmnopqrstuvwxyz";
    private static final String autoRegisterUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String autoRegisterNumbers = "0123456789";
    private static final SecureRandom autoRegisterRandom = new SecureRandom();

    public static boolean isCommon(String password, String playerName, Logger logger) {
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path())) {
            File txtFile = new File("plugins/loginto/rockyou.txt");

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
                logger.error(e.getMessage());
            }
        }

        List<?> listUnknownElements = LoginToFiles.Config.getList(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_LIST.path());
        List<String> list = new ArrayList<>();

        for (Object i : listUnknownElements) {
            if (i instanceof String) {
                String s = (String) i;
                list.add(s);
            }
        }

        if (listUnknownElements.size() != list.size()) {
            logger.error("Some banned password got excluded, make sure that all of them are strings");
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

    public static boolean matchesLengthRequirement(String password, Player player) {
        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_ENABLED.path())) {
            return true;
        }
        int min = LoginToFiles.Config.getInt(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_MIN_LENGTH.path());
        int max = LoginToFiles.Config.getInt(ConfigKeys.PASSWORD_REQUIREMENTS_LENGTH_CHECK_MAX_LENGTH.path());
        if (password.length() < min || password.length() > max) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%min_length%", String.valueOf(min));
            placeholders.put("%max_length%", String.valueOf(max));
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_ERROR_PASSWORD_LENGTH.path(), placeholders));
            return false;
        }
        return true;
    }

    public static boolean doesIncludeReqChars(String password) {

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS.path())) {
            return true;
        }

        final List<String> ReqChar = new ArrayList<>();

        for (char c : (LoginToFiles.Config.getString(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST.path())).toCharArray()) {
            ReqChar.add(String.valueOf(c));
        }

        for (String c : ReqChar) {
            if (!password.contains(c)) {
                return false;
            }
        }

        return true;
    }

    public static String generatePassword() {
        String lower = autoRegisterLower;
        String upper = autoRegisterUpper;
        String numbers = autoRegisterNumbers;
        String allChars = lower + upper + numbers;

        StringBuilder sb = new StringBuilder(10);

        sb.append(lower.charAt(autoRegisterRandom.nextInt(lower.length())));
        sb.append(upper.charAt(autoRegisterRandom.nextInt(upper.length())));
        sb.append(numbers.charAt(autoRegisterRandom.nextInt(numbers.length())));

        for (int i = 3; i < 10; i++) {
            int indiceCasuale = autoRegisterRandom.nextInt(allChars.length());
            sb.append(allChars.charAt(indiceCasuale));
        }
        return sb.toString();
    }
}
