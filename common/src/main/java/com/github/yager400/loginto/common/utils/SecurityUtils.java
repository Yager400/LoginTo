/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.utils;

import com.github.yager400.loginto.common.data.files.FilesManager;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityUtils {

    public static class Hashing {
        public static String hashString(String data) {
            return BCrypt.hashpw(data, BCrypt.gensalt(10));
        }
        public static boolean checkData(String data, String hash) {
            return BCrypt.checkpw(data, hash);
        }
    }

    public static class PasswordSecurity {
        private static final String autoRegisterLower = "abcdefghijklmnopqrstuvwxyz";
        private static final String autoRegisterUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String autoRegisterNumbers = "0123456789";
        private static final SecureRandom autoRegisterRandom = new SecureRandom();

        public static boolean isCommon(String password, Path dataSourcePath, String playerName) {

            Path rockYouPath = Paths.get(dataSourcePath.toFile().getAbsolutePath(), FilesManager.getPluginDataFolderName(), "rockyou.txt");
            FilesManager.downloadRockYou(rockYouPath);
            File txtFile = rockYouPath.toFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals(password)) {
                        return true;
                    }
                }

                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (playerName.equalsIgnoreCase(password)) {
                return true;
            }

            return false;
        }
        public static boolean matchesLengthRequirement(String password, int min, int max) {
            return password.length() >= min && password.length() <= max;
        }

        public static boolean doesIncludeReqChars(String password, char[] requiredCharacters) {
            final List<String> ReqChar = new ArrayList<>();
            for (char c : requiredCharacters) {
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

}
