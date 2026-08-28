/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.players;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Tries {

    private static final HashMap<UUID, Integer> tries = new HashMap<>();

    public static void addPlayer(UUID playerUUID) {
        if (!tries.containsKey(playerUUID)) {
            tries.put(playerUUID, 0);
        }
    }

    public static void removePlayer(UUID playerUUID) {
        tries.remove(playerUUID);
    }

    public static void incrementTries(UUID playerUUID) {
        if (tries.containsKey(playerUUID)) {
            int tries1 = tries.get(playerUUID);
            tries.remove(playerUUID);
            tries.put(playerUUID, tries1 + 1);
        }
    }

    public static boolean isPlayerOutOfTries(UUID playerUUID, int maximumAttempts) {
        return tries.get(playerUUID) >= maximumAttempts;
    }

}
