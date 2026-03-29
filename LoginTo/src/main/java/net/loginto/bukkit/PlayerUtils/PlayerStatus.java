/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.loginto.bukkit.Utils.LoginToFiles;
import net.loginto.bukkit.Utils.ProxyUtils;

public class PlayerStatus {
    
    public static void setPlayerAsLogged(Player player, Plugin plugin, boolean isPremium, boolean teleportBack) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            Sessions.addPlayer(player);
            CompletableFuture.runAsync(() -> { Sessions.Proxy.addPlayerN(player, plugin); });

            Tries.resetTries(player);

            if (teleportBack && LoginToFiles.Config.isFeatureEnabled("spawn-settings.teleport-on-join", plugin) && LoginToFiles.Config.isFeatureEnabled("spawn-settings.restore-previous-location", plugin)) {
                Positions.teleportPlayerToTheOldPos(player, plugin);
            }

            player.updateInventory();

            Logs.logPlayer(player, plugin, isPremium);

            //Effect
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().getId() == PotionEffectType.BLINDNESS.getId()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }

            ProxyUtils.sendPlayerToLobbyPostLogin(plugin, player);
        });
    }

    public static void setPlayerAsNotLogged(Player player, Plugin plugin) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            Sessions.removePlayer(player);

            //Effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE,1), true);
        });
    }

}
