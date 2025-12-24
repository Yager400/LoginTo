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

public class H2 {

    private HikariDataSource dataSource;
    
    private final Plugin plugin;

    public H2(Plugin givedPlugin) {
        this.plugin = givedPlugin;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void connectH2() throws ClassNotFoundException {
        String DBName = getStringFromConfig("database.name", plugin);
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


        if (!doesTableExist()) {
            String createTable = "create table if not exists LoginTo_Users(name varchar(255) primary key not null, password_hash varchar(255) not null);";

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

        String query = "select name from LoginTo_Users where name = ? and password_hash = ? ;";

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

        String query = "insert into LoginTo_Users(name, password_hash) values (?, ?) ;";

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

        String query1 = "select password_hash from LoginTo_Users where password_hash = ? and name = ?;";
        String query2 = "update LoginTo_Users set password_hash = ? where name = ?;";

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
