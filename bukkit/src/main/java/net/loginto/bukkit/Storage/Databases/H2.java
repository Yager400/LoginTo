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
import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.SecurityUtils;
import org.bukkit.plugin.Plugin;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;

public class H2 implements Database {

    private HikariDataSource dataSource;

    private final Plugin plugin;

    public H2(Plugin givedPlugin) {
        this.plugin = givedPlugin;

        try {
            connectH2();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            connectH2();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private void connectH2() throws ClassNotFoundException {
        String DBName = LoginToFiles.Config.getString("storage.database.name", plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";
        File dbFile = new File(plugin.getDataFolder(), DBName);
        String url = "jdbc:h2:" + dbFile.getAbsolutePath().replace("\\", "/");

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("H2 JDBC driver missing!");
            return;
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername("sa");
        cfg.setPassword("");
        cfg.setPoolName("LoginTo-H2");
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(5);
        cfg.setAutoCommit(true);

        dataSource = new HikariDataSource(cfg);


        String createTable = "create table if not exists LoginTo_Users(name varchar(255) primary key not null, password_hash varchar(255) not null, secret varchar(512));";

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

        String query = "insert into LoginTo_Users(name, password_hash) values (?, ?) ;";

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

        String query = "update LoginTo_Users set password_hash = ? where name = ?;";

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
