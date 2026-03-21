/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerStatus {
    
    public static void setPlayerAsLogged(Player player, Plugin plugin, boolean isPremium) {

        Sessions.addPlayer(player);
        Sessions.Proxy.addPlayerN(player, plugin);

        Tries.resetTries(player);

        //Sessions.Proxy.addPlayerN(player, plugin);

        player.updateInventory();

        Logs.logPlayer(player, plugin, isPremium);

        //Effect
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getId() == PotionEffectType.BLINDNESS.getId()) {
                player.removePotionEffect(effect.getType());
                break;
            }
        }
    }

    public static void setPlayerAsNotLogged(Player player) {

        Sessions.removePlayer(player);


        //Effect
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE,1), true);
    }

}
