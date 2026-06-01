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
import net.loginto.bukkit.Utils.JsonToSqlite;
import net.loginto.bukkit.Utils.SecurityUtils;
import org.bukkit.plugin.Plugin;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;

public class SQLite implements Database {

    private HikariDataSource dataSource;

    private final Plugin plugin;

    public SQLite(Plugin givedPlugin) {
        this.plugin = givedPlugin;

        try {
            connectSQLite();
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

    @Override
    public void connect() {
        try {
            connectSQLite();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void connectSQLite() throws ClassNotFoundException {
        String DBName = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_NAME.path(), plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";
        File dbFile = new File(plugin.getDataFolder(), DBName + ".db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace("\\", "/");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLite");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");
        cfg.setAutoCommit(true);

        dataSource = new HikariDataSource(cfg);

        String createTable = "create table if not exists LoginTo_Users(name text primary key not null, password text not null, secret text);";
        executePreparedQuery(createTable);

        if (!doesColumnExists("LoginTo_Users", "secret")) {
            String createSecretColumn = "alter table LoginTo_Users add column secret text;";

            executePreparedQuery(createSecretColumn);
        }


        if (new File(plugin.getDataFolder(), "data.json").exists()) {
            JsonToSqlite.migrate(plugin, dataSource);
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
