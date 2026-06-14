/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.PlayerUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Sessions {
    public static List<UUID> loggedPlayers = new ArrayList<>();
    public static HashMap<UUID, String> borrowedDataFromPreLogin = new HashMap<>();

    public static boolean isPlayerLogged(UUID playerUUID) {
        return loggedPlayers.contains(playerUUID);
    }

    public static void removePlayer(UUID playerUUID) {
        loggedPlayers.remove(playerUUID);
    }

    public static void addPlayer(UUID playerUUID) {
        loggedPlayers.add(playerUUID);
    }

    public static void addBorrowData(UUID playerUUID, String accState) {
        borrowedDataFromPreLogin.put(playerUUID, accState);
    }

    public static String getAccState(UUID playerUUID) {
        return borrowedDataFromPreLogin.get(playerUUID);
    }

    public static void removeBorrowedData(UUID playerUUID) {
        borrowedDataFromPreLogin.remove(playerUUID);
    }
}
