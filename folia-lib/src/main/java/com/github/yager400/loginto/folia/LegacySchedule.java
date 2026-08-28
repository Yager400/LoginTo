/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.folia;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LegacySchedule implements Schedule{

    @Override
    public void teleport(Player player, Location location) {
        Bukkit.getScheduler().runTask(FoliaLib.plugin, () -> {
            player.teleport(location);
        });
    }

    @Override
    public void addPotionEffect(Player player, PotionEffect effect) {
        Bukkit.getScheduler().runTask(FoliaLib.plugin, () -> {
            player.addPotionEffect(effect, true);
        });
    }

    @Override
    public void removePotionEffect(Player player, PotionEffectType potionEffectType) {
        Bukkit.getScheduler().runTask(FoliaLib.plugin, () -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().getId() == potionEffectType.getId()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        });
    }

    @Override
    public void kickPlayer(Player player, String message) {
        player.kickPlayer(message);
    }

    @Override
    public void sendPluginMessage(Player player, String channel, byte[] bytes) {
        player.sendPluginMessage(FoliaLib.plugin, channel, bytes);
    }

    @Override
    public void addItemToInventory(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack);
    }

    @Override
    public void runTask(Runnable run) {
        Bukkit.getScheduler().runTask(FoliaLib.plugin, run);
    }

    @Override
    public void runTaskLater(Runnable run, long delay) {
        Bukkit.getScheduler().runTaskLater(FoliaLib.plugin, run, delay);
    }

    @Override
    public void runTaskTimer(Runnable run, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(FoliaLib.plugin, run, delay, period);
    }

    @Override
    public void runTaskAsync(Runnable run) {
        Bukkit.getScheduler().runTaskAsynchronously(FoliaLib.plugin, run);
    }

    @Override
    public void runTaskLaterAsync(Runnable run, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(FoliaLib.plugin, run, delay);
    }

    @Override
    public void runTaskTimerAsync(Runnable run, long delay, long period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(FoliaLib.plugin, run, delay, period);
    }

    @Override
    public void runAtEntity(Entity entity, Runnable run) {
        runTask(run);
    }

    @Override
    public void runAtEntityLater(Entity entity, Runnable run, long delay) {
        runTaskLater(run, delay);
    }

    @Override
    public void runAtEntityTimer(Entity entity, Runnable run, long delay, long period) {
        runTaskTimer(run, delay, period);
    }
}
