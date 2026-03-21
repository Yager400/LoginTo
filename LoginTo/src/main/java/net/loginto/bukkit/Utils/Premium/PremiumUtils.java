/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Utils.LoginToFiles;

public class PremiumUtils {

    public static class PlayerPremium {
        public static boolean IsPlayerInThePremiumDB(Player player, Plugin plugin) {

            Connection connection = connect(plugin);

            if (connection == null) {
                plugin.getLogger().severe("Connection null");
                return false;
            }

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

            if (connection == null) {
                plugin.getLogger().severe("Connection null");
                return false;
            }

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
    }

    public static class PlayersInfo {
        public static void sendPremiumPluginMessage(OfflinePlayer player, Plugin plugin) {

            if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) return;
            
            Connection conn = connect(plugin);

            try (Connection connection = conn) {
                connection.createStatement().execute("delete from PlayersInfo where username = '" + player.getName() + "'");
                connection.createStatement().execute("insert into PlayersInfo(username, ispremium) values ('" + player.getName() + "', " + true + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        public static void sendCrackedPluginMessage(OfflinePlayer player, Plugin plugin) {

            if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) return;

            Connection conn = connect(plugin);
            
            try (Connection connection = conn) {
                ResultSet set = connection.createStatement().executeQuery("select 1 from PlayersInfo where username = '" + player.getName() + "'");
                if (set.next()) {
                    if (set.getBoolean("ispremium")) return;
                }
                connection.createStatement().execute("insert into PlayersInfo(username, ispremium) values ('" + player.getName() + "', " + false + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        public static void sendRemovePremiumPlayerMessage(OfflinePlayer player, Plugin plugin) {

            if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) return;

            Connection conn = connect(plugin);
            
            try (Connection connection = conn) {
                connection.createStatement().execute("delete from PlayersInfo where username = '" + player.getName() + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    

    public static Connection connect(Plugin plugin) {

        Connection conn = null;

        switch ((String) LoginToFiles.Config.get("premium.storage.database-type", plugin)) {
            case "h2":
                conn = PremiumUtils.H2DB.connect(plugin);
                break;
        
            case "mysql":
                conn = PremiumUtils.MYSQLDB.connect(plugin);
                break;
            
            default:
                plugin.getLogger().severe("Invalid premium database type, change the database type in the config.yml");
                conn = null;
                break;
        }

        return conn;
    }
    



    
    static class H2DB {
        protected static Connection connect(Plugin plugin) {

            Connection conn = null;
            
            String host = (String) LoginToFiles.Config.get("premium.storage.database.host", plugin);
            host = (host != null) ? host : "127.0.0.1";

            int port = (int) LoginToFiles.Config.get("premium.storage.database.port", plugin);
            port = (port != 0) ? port : 9092;

            String url = "jdbc:h2:tcp://" + host + ":" + port + "/./plugins/loginto/LoginTo_Sharing";
            String user = (String) LoginToFiles.Config.get("premium.storage.database.user", plugin);
            String password = (String) LoginToFiles.Config.get("premium.storage.database.password", plugin);
            
            try {
                try {
                    Class.forName("org.h2.Driver");
                    conn = DriverManager.getConnection(url, user, password);
                } catch (SQLException e) {
                    //e.printStackTrace();
                    plugin.getLogger().severe("H2 premium db error code: " + e.getErrorCode());
                    if (e.getErrorCode() == 1049) {
                        try {
                            Class.forName("org.h2.Driver");
                            conn = DriverManager.getConnection(url, user, password);
                        } catch (SQLException ex3) {}
                    } else {
                        plugin.getLogger().warning("Connection error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (conn == null) {
                plugin.getLogger().severe("Could not connect to H2. If you are running the plugin in a standalode bukkit server (not in a proxy), do not use the premium feature. If you are running this plugin in a bukkit server inside a network, check the config.yml file for the database connection.");
                return null;
            }

            return conn;
        }
    }

    static class MYSQLDB {
        @SuppressWarnings("null")
		protected static Connection connect(Plugin plugin) {

            Connection conn = null;
            
            String host = (String) LoginToFiles.Config.get("premium.storage.database.host", plugin);
            host = (host != null) ? host : "127.0.0.1";

            int port = (int) LoginToFiles.Config.get("premium.storage.database.port", plugin);
            port = (port != 0) ? port : 3306;

            String name = (String) LoginToFiles.Config.get("premium.storage.database.database-name", plugin);
            name = (name != null || name.trim().isEmpty()) ? "LoginTo_Sharing" : name;

            String url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&serverTimezone=UTC";
            String user = (String) LoginToFiles.Config.get("premium.storage.database.user", plugin);
            String password = (String) LoginToFiles.Config.get("premium.storage.database.password", plugin);
            
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                plugin.getLogger().severe("MySQL connection error: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                plugin.getLogger().severe("MySQL JDBC Driver not found.");
            }

            if (conn == null) {
                plugin.getLogger().severe("Could not connect to MySQL. If you are running the plugin in a standalode bukkit server (not in a proxy), do not use the premium feature. If you are running this plugin in a bukkit server inside a network, check the config.yml file for the database connection.");
                return null;
            }

            return conn;
        }
    }
}
