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

import java.util.concurrent.TimeUnit;

public class FoliaSchedule implements Schedule{

    @Override
    public void teleport(Player player, Location location) {
        try {
            // Try teleporting the player in the old way
            player.teleport(location);
        } catch (Exception e) {
            player.teleportAsync(location);
        }
    }

    @Override
    public void addPotionEffect(Player player, PotionEffect effect) {
        player.getScheduler().run(FoliaLib.plugin, task -> {
            player.addPotionEffect(effect, true);
        }, null);
    }

    @Override
    public void removePotionEffect(Player player, PotionEffectType potionEffectType) {
        player.getScheduler().run(FoliaLib.plugin, task -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().getId() == potionEffectType.getId()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }, null);
    }

    @Override
    public void kickPlayer(Player player, String message) {
        player.getScheduler().run(FoliaLib.plugin, task -> {
            player.kickPlayer(message);
        }, null);
    }

    @Override
    public void sendPluginMessage(Player player, String channel, byte[] bytes) {
        player.getScheduler().run(FoliaLib.plugin, task -> {
            player.sendPluginMessage(FoliaLib.plugin, channel, bytes);
        }, null);
    }

    @Override
    public void addItemToInventory(Player player, ItemStack itemStack) {
        player.getScheduler().run(FoliaLib.plugin, task -> {
            player.getInventory().addItem(itemStack);
        }, null);
    }

    @Override
    public void runTask(Runnable run) {
        Bukkit.getGlobalRegionScheduler().run(
                FoliaLib.plugin,
                scheduledTask -> run.run()
        );
    }

    @Override
    public void runTaskLater(Runnable run, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(
                FoliaLib.plugin,
                scheduledTask -> run.run(),
                delay
        );
    }

    @Override
    public void runTaskTimer(Runnable run, long delay, long period) {
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(
                FoliaLib.plugin,
                scheduledTask -> run.run(),
                delay,
                period
        );
    }

    @Override
    public void runTaskAsync(Runnable run) {
        Bukkit.getAsyncScheduler().runNow(
                FoliaLib.plugin,
                scheduledTask -> run.run()
        );
    }

    @Override
    public void runTaskLaterAsync(Runnable run, long delay) {
        Bukkit.getAsyncScheduler().runDelayed(
                FoliaLib.plugin,
                scheduledTask -> run.run(),
                Math.max(1, delay) * 50L,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void runTaskTimerAsync(Runnable run, long delay, long period) {
        Bukkit.getAsyncScheduler().runAtFixedRate(
                FoliaLib.plugin,
                scheduledTask -> run.run(),
                Math.max(1, delay) * 50L,
                Math.max(1, period) * 50L,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void runAtEntity(Entity entity, Runnable run) {
        entity.getScheduler().run(FoliaLib.plugin, (task) -> run.run(), null);
    }

    @Override
    public void runAtEntityLater(Entity entity, Runnable run, long delay) {
        entity.getScheduler().runDelayed(FoliaLib.plugin, (task) -> run.run(), null, delay);
    }

    @Override
    public void runAtEntityTimer(Entity entity, Runnable run, long delay, long period) {
        entity.getScheduler().runAtFixedRate(FoliaLib.plugin, (task) -> run.run(), null, delay, period);
    }
}
