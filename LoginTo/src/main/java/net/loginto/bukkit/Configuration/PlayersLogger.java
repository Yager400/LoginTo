/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Config.*;

public class PlayersLogger {
    

    public static void logPlayer(Player player, Plugin plugin, boolean loggedAsPremium) {

        if (!isFeatureEnabled("logging.logging", plugin)) {
            return;
        }

        try {
            File logFile = new File(plugin.getDataFolder(), "log.yml");

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            YamlConfiguration log = YamlConfiguration.loadConfiguration(logFile);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(getStringFromConfig("logging.date-format", plugin));

            if (!log.contains(player.getName())) {
                log.set(player.getName() + ".times-joined", 1);
            }

            int id = log.getInt(player.getName() + ".times-joined");

            log.set(player.getName() + "." + id + ".date", now.format(dateFormat));
            log.set(player.getName() + "." + id + ".loggedAsPremium", loggedAsPremium);
            log.set(player.getName() + ".times-joined", log.getInt(player.getName() + ".times-joined") + 1);

            log.save(logFile);

            
        } catch (Exception e) {e.printStackTrace();}
    }

    public static List<String> getLogs(Player player, Plugin plugin, String dataSelection) {

        List<String> data = new ArrayList<>();

        try {
            File logFile = new File(plugin.getDataFolder(), "log.yml");

            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            YamlConfiguration log = YamlConfiguration.loadConfiguration(logFile);

            int maxId = log.getInt(player.getName() + ".times-joined");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            LocalDate targetDate = null;

            if (!dataSelection.equalsIgnoreCase("n")) {
                targetDate = LocalDate.parse(dataSelection, formatter);
            }

            for (int i = 1; i <= maxId; i++) {

                String dataJoined = log.getString(player.getName() + "." + i + ".date");
                String isPremium = log.getString(player.getName() + "." + i + ".loggedAsPremium");

                if (dataJoined == null) continue;

                String onlyDate = dataJoined.split(" ")[1];

                LocalDate joinedDate;
                try {
                    joinedDate = LocalDate.parse(onlyDate, formatter);
                } catch (Exception e) {
                    continue;
                }

                if (targetDate != null && !joinedDate.equals(targetDate)) continue;

                String information = "Joined: §l" + dataJoined + "§r\nIsPremium: §l" + isPremium + "§r";
                data.add(information);
            }

        } catch (Exception e) {
            data.add("§cTime formatting error, please use dd/MM/yyyy, for example §l'31/12/2025'§r");
        }

        return data;
    }
}
