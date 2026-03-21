/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Utils.LoginToFiles;

public class Tries {
    
    public static HashMap<UUID, Integer> tries = new HashMap<UUID, Integer>();

    public static void addTry(Player player) {
        int triesPlayer = tries.getOrDefault(player.getUniqueId(), 0);
        tries.remove(player.getUniqueId());
        tries.put(player.getUniqueId(), triesPlayer + 1);
    }

    public static void insertPlayerWithZeroTries(Player player) {
        if (!tries.containsKey(player.getUniqueId())) {
            tries.put(player.getUniqueId(), 0);
        }
    }

    public static void resetTries(Player player) {
        if (tries.containsKey(player.getUniqueId())) {
            tries.remove(player.getUniqueId());
        }
    }

    public static boolean triesEnded(Player player, Plugin plugin) {
        int triesPlayer = tries.getOrDefault(player.getUniqueId(), 0);

        if ((int) LoginToFiles.Config.get("auth-security.max-login-attempts", plugin) <= triesPlayer) return true;
        else return false;
    }
}
