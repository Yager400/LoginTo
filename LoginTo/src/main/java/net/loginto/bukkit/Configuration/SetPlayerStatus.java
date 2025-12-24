/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Configuration;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SetPlayerStatus {
    public static void unlockPlayer(Player player) {

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getId() == PotionEffectType.BLINDNESS.getId()) {
                player.removePotionEffect(effect.getType());
                break;
            }
        }
    }

    public static void lockPlayer(Player player) {

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE,1), true);
    }
}
