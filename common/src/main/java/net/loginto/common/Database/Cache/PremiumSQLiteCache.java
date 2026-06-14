/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Database.Cache;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.*;

public class PremiumSQLiteCache {

    private HikariDataSource dataSource;

    public void connect() {

        File dbFolder = new File("plugins/loginto");
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        String url = "jdbc:sqlite:plugins/loginto/cache.db";

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLite");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");
        cfg.setDriverClassName("org.sqlite.JDBC");
        cfg.setAutoCommit(true);

        this.dataSource = new HikariDataSource(cfg);
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public void add(String username, Boolean ispremium) {
        String sql = "insert into cache(username, ispremium, expire) values (?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            if (ispremium) {
                // 1 month of expire date
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setBoolean(2, ispremium);
                    pstmt.setLong(3, ((System.currentTimeMillis() / 1000) + 2678400));
                    pstmt.execute();
                }
            } else {
                // 1 day of expire date
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setBoolean(2, ispremium);
                    pstmt.setLong(3, ((System.currentTimeMillis() / 1000) + 86400));
                    pstmt.execute();
                }
            }
        } catch (SQLException e) {}
    }

    public boolean getPremium(String username) {
        String sql = "select ispremium from cache where username = ?;";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet ser = pstmt.executeQuery();

            while (ser.next()) {
                return ser.getBoolean("ispremium");
            }

        }catch (SQLException e) {}
        return false;
    }

    public boolean isPresent(String username) {
        String sqlSelect = "select * from cache where username = ?;";
        String sqlDelete = "delete from cache where username = ?;";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
            pstmtSelect.setString(1, username);

            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                long expireDate = rs.getLong("expire");
                if ((System.currentTimeMillis() / 1000) > expireDate) {
                    try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)){
                        pstmtDelete.setString(1, username);

                        pstmtDelete.executeUpdate();
                    }
                    return false;
                }
                return rs.getBoolean("ispremium");
            }
        } catch (SQLException e) {}
        return false;
    }

    public void deleteCacheRecord(String username) {
        String sql = "select * from cache where username = ?;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeQuery();
        } catch (SQLException e) {}
    }

}