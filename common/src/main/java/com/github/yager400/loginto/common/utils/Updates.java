/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Updates {

    public static boolean isThereAnUpdate(String currentVersion) {
        try {
            URL url = new URL("https://raw.githubusercontent.com/Yager400/LoginTo/refs/heads/main/build.gradle.kts");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {

                String line;
                String gitVersion = null;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("LoginToVersion")) {
                        gitVersion = line.replaceAll(".*=\\s*\"([^\"]+)\".*", "$1");
                        break;
                    }
                }

                if (gitVersion == null) {
                    return true;
                }

                if (gitVersion.contains("SNAPSHOT")) {
                    return false;
                }

                if (!currentVersion.equals(gitVersion)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
