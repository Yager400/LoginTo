/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration.ConfigMenager;

import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileContent {

    public static String getDefaultConfigFileContent(Plugin plugin) {
        try {
            InputStream in = plugin.getResource("config.yml");
            if (in == null) return "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDefaultMessageFileContent(Plugin plugin) {
        try {
            InputStream in = plugin.getResource("messages.yml");
            if (in == null) return "";

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
