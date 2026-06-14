/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PremiumCache {

    private static HikariDataSource source = null;
    private final long cacheRecordDuration;

    public PremiumCache(Plugin plugin) {
        String cacheDBAbsolutePath = new File(plugin.getDataFolder(), "cache.db").getAbsolutePath().replace("\\", "/");

        String url = "jdbc:sqlite:" + cacheDBAbsolutePath;

        if (source != null) {
            source.close();
            source = null;
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLITEPRCACHE");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");
        cfg.setAutoCommit(true);

        source = new HikariDataSource(cfg);

        String createTableSQL = "create table if not exists PremiumNameCache(name varchar(50) not null unique, isPremium bool not null default false, expire int not null);";

        try (Connection conn = source.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(createTableSQL)) {
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        cacheRecordDuration = (LoginToFiles.Config.getInt(ConfigKeys.PREMIUM_CACHE_DURATION.path(), plugin) * 60L) * 60L;

    }

    public static void closeIfOpen() {
        if (source != null) {
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

    public boolean isCached(String playerName) {
        return getPremiumStatus(playerName) != null;
    }

    public boolean isPremium(String playerName) {
        Boolean status = (Boolean) getPremiumStatus(playerName);
        return status != null && status;
    }

    public void addCachedRecord(String playerName, boolean isPremium) {
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

}
