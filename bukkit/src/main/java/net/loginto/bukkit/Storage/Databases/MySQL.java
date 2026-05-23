/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Storage.Databases;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.loginto.bukkit.Storage.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.SecurityUtils;
import org.bukkit.plugin.Plugin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class MySQL implements Database {

    private HikariDataSource dataSource;

    private final Plugin plugin;

    public MySQL(Plugin givedPlugin) {
        plugin = givedPlugin;

        try {
            connectMySQL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void connect() {
        try {
            connectMySQL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void connectMySQL() throws Exception {

        String host = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_HOST.path(), plugin);
        host = (host != null) ? host : "127.0.0.1";
        int port = LoginToFiles.Config.getInt(ConfigKeys.STORAGE_DATABASE_PORT.path(), plugin);
        String DBName = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";

        String urlNoDB = "jdbc:mysql://" + host + ":" + port + "/";
        String url = urlNoDB + DBName;
        String user = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_USER.path(), plugin);
        String password = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_PASSWORD.path(), plugin);

        plugin.getLogger().info("Connecting to: " + url);

        try {

            try (Connection tmp = DriverManager.getConnection(urlNoDB, user, password)) {
                tmp.prepareStatement("create database if not exists `" + DBName + "`").execute();
            }

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(password);
            cfg.setMaximumPoolSize(10);
            cfg.setMinimumIdle(2);
            cfg.setPoolName("LoginTo-MySQL");
            cfg.addDataSourceProperty("cachePrepStmts", "true");
            cfg.addDataSourceProperty("prepStmtCacheSize", "250");
            cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(cfg);

            plugin.getLogger().info("MySQL connection OK (HikariCP)");

        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Error creating connection pool: " + e.getMessage());
            return;
        }

        String createTable = "create table if not exists LoginTo_Users(name varchar(50) primary key not null, password varchar(512) not null, secret varchar(512));";


        executePreparedQuery(createTable);

        if (!doesColumnExists("LoginTo_Users", "secret")) {
            String createSecretColumn = "alter table LoginTo_Users add column secret varchar(512);";

            executePreparedQuery(createSecretColumn);
        }

    }

    private boolean doesColumnExists(String tableName, String columnName) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void executePreparedQuery(String query) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning("Query error: " + e.getMessage());
        }
    }


    @Override
    public boolean isPlayerPresentInDB(String playerName) {
        String query = "select name from LoginTo_Users where name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isPasswordCorrect(String playerName, String password) throws Exception {

        String query = "select name, password from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.next()) {
                    return false;
                }

                String psw = rs.getString("password");

                try {
                    return BCrypt.checkpw(password, psw);
                } catch (IllegalArgumentException e) {
                    return psw.equals(SecurityUtils.sha256(password));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertPlayer(String playerName, String password) throws Exception {
        String hashedPassword = SecurityUtils.BCrypt(password);

        String query = "insert into LoginTo_Users(name, password) values (?, ?) ;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;


    }

    @Override
    public void changePlayerPassword(String playerName, String newPassword) throws Exception {
        String hashedNewPassword = SecurityUtils.BCrypt(newPassword);

        String query = "update LoginTo_Users set password = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hashedNewPassword);
            pstmt.setString(2, playerName);
            pstmt.execute();
        }
    }

    @Override
    public boolean removePlayer(String playerName) {

        String query = "delete from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        }

    }

    @Override
    public String getSecret(String playerName) {
        String query = "select secret from LoginTo_Users where name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            ResultSet set = pstmt.executeQuery();

            if (set.next()) {
                return set.getString("secret");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSecret(String playerName, String secret) {
        String query = "update LoginTo_Users set secret = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, secret);
            pstmt.setString(2, playerName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
