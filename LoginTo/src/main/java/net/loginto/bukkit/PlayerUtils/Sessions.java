/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PremiumUtils;

public class Sessions {
    
    public static List<UUID> loggedPlayers = new ArrayList<>();

    public static boolean isPlayerLogged(Player player) {
        return loggedPlayers.contains(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
        loggedPlayers.remove(player.getUniqueId());
    }

    public static void addPlayer(Player player) {
        loggedPlayers.add(player.getUniqueId());
    }

    public static class Proxy {

        public static boolean isPlayerLoggedN(Player player, Plugin plugin) {
            if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) return false;

            Connection connection = PremiumUtils.connect(plugin);

            if (connection == null) {
                plugin.getLogger().severe("Connection null");
                return false;
            }

            String sql = "select * from LoggedPlayers where username = ?";

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

        public static void addPlayerN(Player player, Plugin plugin) {
            if (!(Boolean) LoginToFiles.Config.get("premium.enable-premium-features", plugin)) return;

            Connection connection = PremiumUtils.connect(plugin);

            if (connection == null) {
                plugin.getLogger().severe("Connection null");
                return;
            }

            String sql = "insert into LoggedPlayers(username) values (?)";

            try (Connection conn = connection;
                PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, player.getName());

                ps.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
