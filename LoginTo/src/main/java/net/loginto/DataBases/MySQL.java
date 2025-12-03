/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.DataBases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.ExtraFeature.Utility.*;

import static net.loginto.Configuration.Config.*;

public class MySQL {

    private Connection conn;

    private final Plugin plugin;

    public MySQL(Plugin givedPlugin) {
        plugin = givedPlugin;
    }

    public void close() {
        if (conn == null) return;
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void connectMySQL() throws Exception {
        
        String host = getStringFromConfig("database.host", plugin);
        host = (host != null) ? host : "127.0.0.1";
        int port = getIntFromConfig("database.port", plugin);
        String DBName = getStringFromConfig("database.name", plugin);
        DBName = (DBName != null) ? DBName : "LoginTo_DB";

        String urlNoDB = "jdbc:mysql://" + host + ":" + port + "/";
        String url = urlNoDB + DBName;
        String user = getStringFromConfig("database.user", plugin);
        String password = getStringFromConfig("database.password", plugin);
        
        plugin.getLogger().info("Connecting to: " + url);
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().warning(e.getErrorCode() + "");
            if (e.getErrorCode() == 1049) {

                try (Connection tmp = DriverManager.getConnection(urlNoDB, user, password)) {
                    tmp.prepareStatement("create database " + DBName).execute();
                } catch (SQLException ee) {}

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    conn = DriverManager.getConnection(url, user, password);
                } catch (SQLException ex3) {}

            } else {
                plugin.getLogger().warning("Connection error: " + e.getMessage());
            }
        }

        if (conn == null) {
            plugin.getLogger().severe("Could not connect to MySQL. Check config");
            return;
        }

        if (!doesTableExist()) {
            
            String createTable = "create table LoginTo_Users(name varchar(50) primary key not null, password varchar(512) not null);";
            executePreparedQuery(createTable);
        }

        
    }


    public boolean doesTableExist() {
        String q = "SHOW TABLES LIKE 'LoginTo_Users'";
        try (PreparedStatement ps = conn.prepareStatement(q);
            ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void executePreparedQuery(String query) {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
        } catch (SQLException e ){
            e.printStackTrace();
            plugin.getLogger().warning("Query error: " + e.getMessage());
        }
    }

    





    public boolean isPlayerPresentInDB(Player player) {
        String query = "select name from LoginTo_Users where name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, player.getName());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return false;
    }

    public boolean isPasswordCorrect(Player player, String password) throws Exception {
        String hashedPassword = sha256(password);

        String query = "select name from LoginTo_Users where name = ? and password = ? ;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
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

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, player.getName());
            pstmt.setString(2, hashedPassword);

            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }



    }

    public String changePlayerPassword(Player player, String oldPassword, String newPassword) throws Exception {
        String hachedOldPassword = sha256(oldPassword);
        String hashedNewPassword = sha256(newPassword);

        String query1 = "select password from LoginTo_Users where password = ? and name = ?;";
        String query2 = "update LoginTo_Users set password = ? where name = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query1)) {
            pstmt.setString(1, hachedOldPassword);
            pstmt.setString(2, player.getName());

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return "WRONGPSW";
            }
            else {

                try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
                    pstmt2.setString(1, hashedNewPassword);
                    pstmt2.setString(2, player.getName());

                    pstmt2.executeUpdate();

                    return "OK";

                } catch (SQLException e) {
                    return "DBERR2";
                }

            }


        } catch (SQLException e) {
            return "DBERR1";
        }
    }

    public boolean removePlayer(Player player) {

        String query = "delete from LoginTo_Users where name = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, player.getName());

            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            return false;
        }
        
    }

     
}
