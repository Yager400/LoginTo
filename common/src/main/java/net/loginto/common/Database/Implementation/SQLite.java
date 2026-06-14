/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Database.Implementation;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.loginto.common.Database.Database;
import net.loginto.common.Utils.SecurityUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.*;

public class SQLite implements Database {

    private HikariDataSource dataSource;

    public SQLite(String databaseName) {

        try {
            connectSQLite(databaseName);
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
    public boolean ping() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void connect(String host, int port, String password, String username, String databaseName) {
        try {
            connectSQLite(databaseName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void connectSQLite(String databaseName) throws ClassNotFoundException {
        databaseName = (databaseName != null) ? databaseName : "LoginTo_DB";
        File dbFile = new File("plugins/loginto/", databaseName + ".db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace("\\", "/");

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

        dataSource = new HikariDataSource(cfg);

        String createTable = "create table if not exists LoginTo_Users(name text primary key not null, password text not null, isPremium boolean not null, isBedrock boolean not null, secret text);";

        executePreparedQuery(createTable);

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

                return BCrypt.checkpw(password, psw);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean insertPlayer(String playerName, String password) throws Exception {
        String hashedPassword = SecurityUtils.BCrypt(password);

        String query = "insert into LoginTo_Users(name, password, isPremium, isBedrock) values (?, ?, ?, ?) ;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            pstmt.setString(2, hashedPassword);
            pstmt.setBoolean(3, false);
            pstmt.setBoolean(4, false);

            pstmt.execute();

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
    public boolean canBypassLogin(String playerName) {
        String query = "select isPremium, isBedrock from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            ResultSet set = pstmt.executeQuery();
            while (set.next()) {
                return set.getBoolean("isPremium") || set.getBoolean("isBedrock");
            }

            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean isPremium(String playerName) {
        String query = "select isPremium from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            ResultSet set = pstmt.executeQuery();
            while (set.next()) {
                return set.getBoolean("isPremium");
            }

            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void chanceAccStatusToPremium(String playerName, boolean isPremium) {
        String query = "update LoginTo_Users set isPremium = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isPremium);
            pstmt.setString(2, playerName);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void chanceAccStatusToBedrock(String playerName, boolean isBedrock) {
        String query = "update LoginTo_Users set isBedrock = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, isBedrock);
            pstmt.setString(2, playerName);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSecret(String playerName, String secret) {
        String query = "update LoginTo_Users set secret = ? where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, secret);
            pstmt.setString(2, playerName);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSecret(String playerName) {
        String query = "select secret from LoginTo_Users where name = ?;";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, playerName);
            ResultSet set = pstmt.executeQuery();
            while(set.next()) {
                return set.getString("secret");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
