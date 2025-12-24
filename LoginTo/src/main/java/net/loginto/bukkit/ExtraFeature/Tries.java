/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.ExtraFeature;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.Messages.*;
import static net.loginto.bukkit.Configuration.Config.*;

public class Tries {
    
    public static HashMap<Player, Integer> tries = new HashMap<>();

    public static void checkTries(Player player, Plugin plugin) {
        if (tries.containsKey(player)) {
            
            if (isFeatureEnabled("kick-rules.kick_on_wrong_password", plugin)) {
                if (tries.get(player) >= getIntFromConfig("kick-rules.tries", plugin)) {
                    player.kickPlayer(getMessage("errors.onkick_for_failed_login", plugin));
                }
            }
        }
        
    }

    public static void addPlayerTries(Player player, Plugin plugin) {
        if (!tries.containsKey(player)) {
            tries.put(player, 0);
        }
    }

    public static void incrementTries(Player player, Plugin plugin) {
        if (tries.containsKey(player)) {
            int current = tries.getOrDefault(player, 0);
            tries.put(player, current + 1);
        }
        checkTries(player, plugin);
    }

    public static void removePlayerTries(Player player, Plugin plugin) {
        if (tries.containsKey(player)) {
            tries.remove(player);
        }
    }
}
