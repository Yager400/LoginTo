/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class LoggedPlayers {

    public static List<Player> loggedPlayers = new ArrayList<>();

    public static void addPlayer(Player player) {
        loggedPlayers.add(player);
    }

    public static void removePlayer(Player player) {
        loggedPlayers.remove(player);
    }

    public static boolean isPlayerLogged(Player player) {
        return loggedPlayers.contains(player);
    }
}
