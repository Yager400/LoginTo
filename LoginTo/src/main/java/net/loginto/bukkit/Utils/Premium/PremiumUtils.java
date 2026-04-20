/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.loginto.bukkit.Utils.LoginToFiles;

public class PremiumUtils {

    private static HikariDataSource source;

    public static class PlayerPremium {
        public static boolean IsPlayerInThePremiumDB(Player player, Plugin plugin) {

            if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) return false;

            HikariDataSource src = connectAndGetSource(plugin);

            if (src == null) {
                plugin.getLogger().severe("Source null");
                return false;
            }

            String sql = "select 1 from PlayersInfo where username = ?";

            try (Connection conn = src.getConnection();
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

            if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) return false;

            HikariDataSource src = connectAndGetSource(plugin);

            if (src == null) {
                plugin.getLogger().severe("Source null");
                return false;
            }

            String user = player.getName();

            try (Connection conn = src.getConnection()) {

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

                String sqlInfo = "select ispremium from PlayersInfo where username = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
                    ps.setString(1, user);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || !rs.getBoolean("ispremium")) {
                            return false;
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
        public static void sendPremiumRequest(OfflinePlayer player, Plugin plugin) {

            if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) return;

            try (Connection connection = connectAndGetSource(plugin).getConnection()) {
                connection.createStatement().execute("delete from PlayersInfo where username = '" + player.getName() + "'");
                connection.createStatement().execute("insert into PlayersInfo(username, ispremium) values ('" + player.getName() + "', " + true + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        public static void sendCrackedRequest(OfflinePlayer player, Plugin plugin) {

            if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) return;
            
            try (Connection connection = connectAndGetSource(plugin).getConnection()) {
                ResultSet set = connection.createStatement().executeQuery("select 1 from PlayersInfo where username = '" + player.getName() + "'");
                if (set.next()) {
                    if (set.getBoolean("ispremium")) return;
                }
                connection.createStatement().execute("insert into PlayersInfo(username, ispremium) values ('" + player.getName() + "', " + false + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        public static void sendRemovePremiumRequest(OfflinePlayer player, Plugin plugin) {

            if (!LoginToFiles.Config.isFeatureEnabled("premium.enable-premium-features", plugin)) return;
            
            try (Connection connection = connectAndGetSource(plugin).getConnection()) {
                connection.createStatement().execute("delete from PlayersInfo where username = '" + player.getName() + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    

    public static HikariDataSource connectAndGetSource(Plugin plugin) {

        if (source == null) {
            switch (LoginToFiles.Config.getString("premium.storage.database-type", plugin)) {
                case "h2":
                    source = new HikariDataSource(PremiumUtils.H2DB.connect(plugin));
                    break;
            
                case "mysql":
                    source = new HikariDataSource(PremiumUtils.MYSQLDB.connect(plugin));
                    break;
                
                default:
                    plugin.getLogger().severe("Invalid premium database type, change the database type in the config.yml");
                    source = null;
                    break;
            }
        }

        return source;
    }

    public static void close() {
        if (source != null) {
            source.close();
        }
    }
    
    static class H2DB {
        protected static HikariConfig connect(Plugin plugin) {

            String host = LoginToFiles.Config.getString("premium.storage.database.host", plugin);
            host = (host != null) ? host : "127.0.0.1";

            int port = LoginToFiles.Config.getInt("premium.storage.database.port", plugin);
            port = (port != 0) ? port : 9092;

            String url = "jdbc:h2:tcp://" + host + ":" + port + "/./plugins/loginto/LoginTo_Sharing";
            String user = LoginToFiles.Config.getString("premium.storage.database.user", plugin);
            String password = LoginToFiles.Config.getString("premium.storage.database.password", plugin);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("org.h2.Driver");
            config.setPoolName("H2-Hikari");

            return config;
        }
    }

    static class MYSQLDB {
		protected static HikariConfig connect(Plugin plugin) {

            String host = LoginToFiles.Config.getString("premium.storage.database.host", plugin);
            host = (host != null) ? host : "127.0.0.1";

            int port = LoginToFiles.Config.getInt("premium.storage.database.port", plugin);
            port = (port != 0) ? port : 3306;

            String name = LoginToFiles.Config.getString("premium.storage.database.database-name", plugin);
            name = (name != null) ? name : "LoginTo_Sharing";
            /*
            if (name == null || name.trim().isEmpty()) {
                name = "LoginTo_Sharing";
            }
                 */

            String url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false&serverTimezone=UTC";
            String user = LoginToFiles.Config.getString("premium.storage.database.user", plugin);
            String password = LoginToFiles.Config.getString("premium.storage.database.password", plugin);


            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(5000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setPoolName("MySQL-Hikari");

            return config;
        }
    }
}
