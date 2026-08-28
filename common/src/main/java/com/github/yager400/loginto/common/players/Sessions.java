/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.players;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Sessions {

    private static final Set<UUID> loggedPlayers = new HashSet<>();

    public static void addPlayer(UUID playerUUID) {
        loggedPlayers.add(playerUUID);
    }

    public static void removePlayer(UUID playerUUID) {
        loggedPlayers.remove(playerUUID);
    }

    public static boolean isPlayerLogged(UUID playerUUID) {
        return loggedPlayers.contains(playerUUID);
    }
}
