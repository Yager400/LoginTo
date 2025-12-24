/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLite {
    
    private Connection conn;
    private Statement stmt;

    public void connect() {

        File dbFolder = new File("plugins/loginto");
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        File dbFile = new File(dbFolder, "cache.db");

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(
                "jdbc:sqlite:" + dbFile.getAbsolutePath()
            );

            stmt = conn.createStatement();

            stmt.executeUpdate("create table if not exists cache(username varchar(255) unique not null, ispremium boolean not null, expire bigint not null);");



        } catch (SQLException | ClassNotFoundException e) {e.printStackTrace();}
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {e.printStackTrace();}
        }
    }

    public void add(String username, Boolean ispremium) {
        try {
            if (ispremium) {
                // 1 month of expire date
                stmt.executeUpdate("insert into cache(username, ispremium, expire) values ('" + username + "', " + ispremium + ", " + ((System.currentTimeMillis() / 1000) + 2678400) + ");");
            } else {
                // 1 day of expire date
                stmt.executeUpdate("insert into cache(username, ispremium, expire) values ('" + username + "', " + ispremium + ", " + ((System.currentTimeMillis() / 1000) + 86400) + ");");
            }
        } catch (SQLException e) {e.printStackTrace();}
    }

    public boolean getPremium(String username) {
        try {
            ResultSet rs = stmt.executeQuery("select ispremium from cache where username = '" + username + "';");
            while (rs.next()) {
                return rs.getBoolean("ispremium");
            }
        } catch (SQLException e) {e.printStackTrace();}
        return false;
    }

    public boolean isPresent(String username) {
        try {
            ResultSet rs = stmt.executeQuery("select * from cache where username = '" + username + "';");
            if (rs.next()) {
                long expireDate = rs.getLong("expire");
                if ((System.currentTimeMillis() / 1000) > expireDate) {
                    stmt.executeUpdate("delete from cache where username = '" + username + "';");
                    return false;
                }
                return rs.getBoolean("ispremium");
            }
        } catch (SQLException e) {e.printStackTrace();}
        return false;
    }



}
