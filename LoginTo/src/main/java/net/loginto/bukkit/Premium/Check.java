/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Premium;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Config.*;

public class Check {

    public static boolean IsPlayerInThePremiumDB(Player player, Plugin plugin) {

        Connection connection = connect(plugin);

        if (connection == null) return false;

        String sql = "select 1 from PlayersInfo where username = ?";

        try (Connection conn = connection;
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player.getName());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean CheckIfAPlayerCanAutoLogin(Player player, Plugin plugin) {

        Connection connection = connect(plugin);

        if (connection == null) return false;

        String user = player.getName();

        try (Connection conn = connection) {

            String sqlInfo = "select ispremium from PlayersInfo where username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
                ps.setString(1, user);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !rs.getBoolean("ispremium")) {
                        return false;
                    }
                }
            }

            String sqlAuth = "select ispremium from authplayers where username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAuth)) {
                ps.setString(1, user);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        plugin.getLogger().info(String.valueOf(rs.getBoolean("ispremium")));
                        return rs.getBoolean("ispremium");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private static Connection connect(Plugin plugin) {

        Connection conn = null;
        
        String host = getStringFromConfig("premium.database.host", plugin);
        host = (host != null) ? host : "127.0.0.1";

        int port = getIntFromConfig("premium.database.port", plugin);
        port = (port != 0) ? port : 9092;

        String url = "jdbc:h2:tcp://" + host + ":" + port + "/./plugins/loginto/LoginTo_Sharing";
        String user = getStringFromConfig("premium.database.user", plugin);
        String password = getStringFromConfig("premium.database.password", plugin);
        
        plugin.getLogger().info("Connecting to: " + url + " (Premium database)");
        
        try {
            try {
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().warning(e.getErrorCode() + "");
                if (e.getErrorCode() == 1049) {
                    try {
                        Class.forName("org.h2.Driver");
                        conn = DriverManager.getConnection(url, user, password);
                    } catch (SQLException ex3) {}
                } else {
                    plugin.getLogger().warning("Connection error: " + e.getMessage());
                }
            }
        } catch (Exception e) {}

        if (conn == null) {
            plugin.getLogger().severe("Could not connect to H2. Check config");
            return null;
        }

        return conn;
    }
    
}
