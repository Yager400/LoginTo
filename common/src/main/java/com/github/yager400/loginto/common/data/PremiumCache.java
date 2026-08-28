/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data;

import com.github.yager400.loginto.common.data.files.FilesManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PremiumCache {

    private static HikariDataSource source = null;
    private final long cacheRecordDuration;

    public PremiumCache(Path pluginDataFolderPath, int cacheDurationHours) {
        String cacheDBAbsolutePath = Paths.get(pluginDataFolderPath.toFile().getAbsolutePath(), FilesManager.getPluginDataFolderName(), "cache.db").toString().replace("\\", "/");

        String url = "jdbc:sqlite:" + cacheDBAbsolutePath;

        if (source != null) {
            source.close();
            source = null;
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLITEPRCACHE");
        cfg.setDriverClassName("org.sqlite.JDBC");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");
        cfg.setAutoCommit(true);

        source = new HikariDataSource(cfg);

        String createTableSQL = "create table if not exists PremiumNameCache(name varchar(50) not null unique, isPremium bool not null default false, expire int not null);";
        String createNameToUUIDTable = "CREATE TABLE IF NOT EXISTS NameToUUIDCache(name varchar(50) not null unique, uuid text not null, expire int not null);";

        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(createTableSQL);
             PreparedStatement pstmt2 = conn.prepareStatement(createNameToUUIDTable)) {
            pstmt.execute();
            pstmt2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cacheRecordDuration = cacheDurationHours * 60L * 60L;

    }

    public static void closeIfOpen() {
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }

    private Object getPremiumStatus(String playerName) {
        String sql = "SELECT isPremium, expire FROM PremiumNameCache WHERE name = ?;";
        String deleteSQL = "DELETE FROM PremiumNameCache WHERE name = ?;";

        try (Connection conn = source.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            try (ResultSet set = pstmt.executeQuery()) {
                if (set.next()) {
                    if (set.getLong("expire") < (System.currentTimeMillis() / 1000)) {
                        try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {
                            pstmtDelete.setString(1, playerName);
                            pstmtDelete.execute();
                        }
                        return null;
                    }
                    return set.getBoolean("isPremium");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPremiumCached(String playerName) {
        return getPremiumStatus(playerName) != null;
    }

    public boolean isPremium(String playerName) {
        Boolean status = (Boolean) getPremiumStatus(playerName);
        return status != null && status;
    }

    public void addPremiumCachedRecord(String playerName, boolean isPremium) {
        String sql = "INSERT INTO PremiumNameCache(name, isPremium, expire) VALUES (?, ?, ?);";

        try (Connection conn = source.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setBoolean(2, isPremium);
            pstmt.setLong(3, (System.currentTimeMillis() / 1000 + cacheRecordDuration));

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object getUUIDFromNameStatus(String playerName) {
        String sql = "SELECT uuid, expire FROM NameToUUIDCache WHERE name = ?;";
        String deleteSQL = "DELETE FROM NameToUUIDCache WHERE name = ?;";

        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);

            try (ResultSet set = pstmt.executeQuery()) {
                if (set.next()) {
                    if (set.getLong("expire") < (System.currentTimeMillis() / 1000)) {
                        try (PreparedStatement pstmtDelete = conn.prepareStatement(deleteSQL)) {
                            pstmtDelete.setString(1, playerName);
                            pstmtDelete.execute();
                        }
                        return null;
                    }
                    return set.getString("uuid");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isUUIDFromNameCached(String playerName) {
        return getUUIDFromNameStatus(playerName) != null;
    }

    public UUID getUUIDFromName(String playerName) {
        return UUID.fromString((String) getUUIDFromNameStatus(playerName));
    }

    public void addUUIDFromNameCachedRecord(String playerName, UUID uuid) {
        String sql = "INSERT INTO NameToUUIDCache(name, uuid, expire) VALUES (?, ?, ?);";

        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, uuid.toString());
            pstmt.setLong(3, (System.currentTimeMillis() / 1000 + cacheRecordDuration));

            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
