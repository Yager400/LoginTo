/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.DataBases;

import java.io.File;
import java.sql.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import static net.loginto.bukkit.Configuration.Config.*;
import static net.loginto.bukkit.ExtraFeature.Utility.*;

public class SQLite {

    private HikariDataSource dataSource;

    private final Plugin plugin;

    public SQLite(Plugin givedPlugin) {
        this.plugin = givedPlugin;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }


    public void connectSQLite() throws ClassNotFoundException {
        String DBName = getStringFromConfig("database.name", plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";
        File dbFile = new File(plugin.getDataFolder(), DBName + ".db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath().replace("\\", "/");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver missing!");
            return;
        }

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setPoolName("LoginTo-SQLite");
        cfg.setMaximumPoolSize(1);
        cfg.setMinimumIdle(1);
        cfg.setConnectionTestQuery("SELECT 1");
        cfg.addDataSourceProperty("journal_mode", "WAL");
        cfg.addDataSourceProperty("foreign_keys", "on");

        dataSource = new HikariDataSource(cfg);

        if (!doesTableExist()) {
            String createTable = "create table if not exists LoginTo_Users(name text primary key not null, password text not null);";

            executePreparedQuery(createTable);
        }
    }



    private boolean doesTableExist() {
        try (Connection conn = dataSource.getConnection()) {

            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet rs = meta.getTables(null, null, "LoginTo_Users", null)) {
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
            conn.commit();
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
                conn.commit();
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
            conn.commit();
            return true;

        } catch (SQLException e) {
            return false;
        }
        
    }

     
}
