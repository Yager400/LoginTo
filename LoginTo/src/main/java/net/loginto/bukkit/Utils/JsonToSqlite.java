/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariDataSource;

public class JsonToSqlite {
    

    public static void migrate(Plugin plugin, HikariDataSource source) {

        plugin.getLogger().info("Migrating from json to sqlite...");

        File jsonFile = new File(plugin.getDataFolder(), "data.json");
        if (!jsonFile.exists()) return;

        JsonManager json = new JsonManager(plugin.getDataFolder(), "data.json");

        String sql = "insert or ignore into LoginTo_Users (name, password) values (?, ?)";

        try (Connection conn = source.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String username : json.getAllKeys()) {
                String password = Hash.BCrypt(json.getString(username + ".password"));

                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            plugin.getLogger().severe("Migration error: " + e.getMessage());
            e.printStackTrace();
        }
        jsonFile.renameTo(new File(plugin.getDataFolder(), "backup_data.json"));

        plugin.getLogger().info("Migrating finished successfully");

    }
}
