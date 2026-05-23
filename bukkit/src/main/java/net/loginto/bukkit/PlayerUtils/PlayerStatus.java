/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.ProxyUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.CompletableFuture;

public class PlayerStatus {

    @SuppressWarnings("deprecation")
    public static void setPlayerAsLogged(Player player, Plugin plugin, boolean isPremium, boolean teleportBack) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            Sessions.addPlayer(player);
            CompletableFuture.runAsync(() -> {
                Sessions.Proxy.addPlayerN(player, plugin);
            });

            Tries.resetTries(player);

            if (teleportBack && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.SPAWN_SETTINGS_TELEPORT_ON_JOIN.path(), plugin) && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.SPAWN_SETTINGS_RESTORE_PREVIOUS_LOCATION.path(), plugin)) {
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

    @SuppressWarnings("deprecation")
    public static void setPlayerAsNotLogged(Player player, Plugin plugin) {

        Bukkit.getScheduler().runTask(plugin, () -> {
            Sessions.removePlayer(player);

            //Effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1), true);
        });
    }

}
