/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.PlayerUtils;

import java.util.HashMap;
import java.util.UUID;

public class Tries {
    public static HashMap<UUID, Integer> tries = new HashMap<UUID, Integer>();

    public static void addTry(UUID playerUUID) {
        int triesPlayer = tries.getOrDefault(playerUUID, 0);
        tries.remove(playerUUID);
        tries.put(playerUUID, triesPlayer + 1);
    }

    public static void insertPlayerWithZeroTries(UUID playerUUID) {
        if (!tries.containsKey(playerUUID)) {
            tries.put(playerUUID, 0);
        }
    }

    public static void resetTries(UUID playerUUID) {
        if (tries.containsKey(playerUUID)) {
            tries.remove(playerUUID);
        }
    }

    public static boolean triesEnded(UUID playerUUID, int maxAuthAttempt) {
        int triesPlayer = tries.getOrDefault(playerUUID, 0);

        if (maxAuthAttempt <= triesPlayer) {
            return true;
        } else {
            return false;
        }
    }
}
