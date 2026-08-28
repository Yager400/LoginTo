/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.database.migrations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

// Tool for migrating from an old LoginTo database (Version 3.x) to a new database with newer columns (Version 4.0)
public class V3_8ToV4_0SQLite {

    public static boolean canMigrate(Path pluginDataSourcePath) {
        File oldSQLiteDatabase = new File(pluginDataSourcePath.toFile(), "LoginTo_DB.db");
        return oldSQLiteDatabase.exists();
    }

    public static void migrate(Path pluginDataSourcePath, HikariDataSource source) {
        File oldSQLiteDatabase = new File(pluginDataSourcePath.toFile(), "LoginTo_DB.db");
        File cacheToDelete = new File(pluginDataSourcePath.toFile(), "cache.db");
        if (cacheToDelete.exists()) { cacheToDelete.delete(); }

        HikariConfig oldDBConfig = new HikariConfig();
        oldDBConfig.setJdbcUrl("jdbc:sqlite:" + oldSQLiteDatabase.getAbsolutePath());
        oldDBConfig.setPoolName("LoginTo_OLDSQLite_Pool");
        oldDBConfig.setConnectionTestQuery("SELECT 1");
        oldDBConfig.setAutoCommit(true);
        oldDBConfig.setMinimumIdle(2);
        oldDBConfig.setMaximumPoolSize(5);
        oldDBConfig.addDataSourceProperty("journal_mode", "WAL");
        oldDBConfig.addDataSourceProperty("foreign_keys", "on");
        HikariDataSource oldDBSource = new HikariDataSource(oldDBConfig);

        boolean success = true;

        try (Connection oldConn = oldDBSource.getConnection();
            Connection conn = source.getConnection()) {

            ResultSet set = oldConn.prepareStatement("SELECT * FROM LoginTo_Users").executeQuery();
            while (set.next()) {
                try (PreparedStatement isPremiumPstmt = oldConn.prepareStatement("SELECT * FROM LoginTo_PremiumAccStatus WHERE name = ?;")) {
                    isPremiumPstmt.setString(1, set.getString("name"));
                    ResultSet premiumSet = isPremiumPstmt.executeQuery();
                    boolean isPremium = premiumSet.next() && premiumSet.getBoolean("isPremium");

                    UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + set.getString("name")).getBytes(StandardCharsets.UTF_8));
                    String password = set.getString("password");
                    String secret = set.getString("secret");

                    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO LoginTo_Players_Accounts(uuid, password, secret, isPremium) VALUES (?, ?, ?, ?);")) {
                        pstmt.setString(1, offlineUUID.toString());
                        pstmt.setString(2, password);
                        pstmt.setString(3, secret);
                        pstmt.setBoolean(4, isPremium);
                        pstmt.execute();
                    }
                }
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        if (!oldDBSource.isClosed()) {
            oldDBSource.close();
        }

        // Check for success after every connection is closed, otherwise the wile won't move
        if (success) {
            try {
                Path oldDatabaseMovingFile = Paths.get(pluginDataSourcePath.toFile().getAbsolutePath(), "_pluginData", "LoginTo_DB.db_BACKUP");

                Files.move(oldSQLiteDatabase.toPath(), oldDatabaseMovingFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
