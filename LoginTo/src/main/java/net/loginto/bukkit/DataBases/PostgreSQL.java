/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.DataBases;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static net.loginto.bukkit.Configuration.Config.*;

import static net.loginto.bukkit.ExtraFeature.Utility.*;

public class PostgreSQL {
     
    private HikariDataSource dataSource;

    private final Plugin plugin;

    public PostgreSQL(Plugin givedPlugin) {
        plugin = givedPlugin;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


    public void connectPostgreSQL() throws Exception {
        
        String host = getStringFromConfig("database.host", plugin);
        host = (host != null) ? host : "127.0.0.1";
        int port = getIntFromConfig("database.port", plugin);
        String DBName = getStringFromConfig("database.name", plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";

        String urlNoDB = "jdbc:postgresql://" + host + ":" + port + "/";
        String url = urlNoDB + DBName;
        String user = getStringFromConfig("database.user", plugin);
        String password = getStringFromConfig("database.password", plugin);

        plugin.getLogger().info("Connecting to: " + url);

        try (Connection ignored =
            java.sql.DriverManager.getConnection(url, user, password)) {
        } catch (SQLException e) {

            if ("3D000".equals(e.getSQLState())) {
                try (Connection tmp =
                    DriverManager.getConnection(urlNoDB + "postgres", user, password)) {
                    tmp.prepareStatement("create database \"" + DBName + "\"").execute();
                }
            } else {
                plugin.getLogger().severe("Connection error: " + e.getMessage());
                return;
            }
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setPoolName("LoginTo-PostgreSQL");
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(cfg);

        String createTable =
            "CREATE TABLE IF NOT EXISTS LoginTo_Users(" +
            "name VARCHAR(50) PRIMARY KEY NOT NULL," +
            "password VARCHAR(512) NOT NULL" +
            ");";


        executePreparedQuery(createTable);
    }

    private void executePreparedQuery(String query) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

    





    public boolean isPlayerPresentInDB(Player player) {
        String query = "select name from LoginTo_Users where name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPasswordCorrect(Player player, String password) throws Exception {
        String hashedPassword = sha256(password);

        String query = "select name from LoginTo_Users where name = ? and password = ? ;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            pstmt.setString(2, hashedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean insertPlayer(Player player, String password) throws Exception {
        String hashedPassword = sha256(password);

        String query = "insert into LoginTo_Users(name, password) values (?, ?) ;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            pstmt.setString(2, hashedPassword);

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;



    }

    public String changePlayerPassword(Player player, String oldPassword, String newPassword) throws Exception {
        String hachedOldPassword = sha256(oldPassword);
        String hashedNewPassword = sha256(newPassword);

        String query1 = "select password from LoginTo_Users where password = ? and name = ?;";
        String query2 = "update LoginTo_Users set password = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query1)) {

            pstmt.setString(1, hachedOldPassword);
            pstmt.setString(2, player.getName());

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next())
                return "WRONGPSW";

            try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                pstmt2.setString(1, hashedNewPassword);
                pstmt2.setString(2, player.getName());
                pstmt2.executeUpdate();
                return "OK";

            } catch (SQLException e) {
                return "DBERR2";
            }

        } catch (SQLException e) {
            return "DBERR1";
        }
    }

    public boolean removePlayer(Player player) {

        String query = "delete from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }
        
    }

     
}
