/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import org.bukkit.entity.Player;

import java.util.*;

public class Sessions {

    public static Set<UUID> loggedPlayers = new HashSet<>();

    public static boolean isPlayerLogged(Player player) {
        return loggedPlayers.contains(player.getUniqueId());
    }

    public static void removePlayer(Player player) {
        loggedPlayers.remove(player.getUniqueId());
    }

    public static void addPlayer(Player player) {
        loggedPlayers.add(player.getUniqueId());
    }
}
