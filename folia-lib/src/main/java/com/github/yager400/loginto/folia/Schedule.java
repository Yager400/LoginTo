/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.folia;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface Schedule {

    void teleport(Player player, Location location);
    void addPotionEffect(Player player, PotionEffect effect);
    void removePotionEffect(Player player, PotionEffectType effectType);
    void kickPlayer(Player player, String message);
    void sendPluginMessage(Player player, String channel, byte[] bytes);
    void addItemToInventory(Player player, ItemStack itemStack);
    void runTask(Runnable run);
    void runTaskLater(Runnable run, long delay);
    void runTaskTimer(Runnable run, long delay, long period);
    void runTaskAsync(Runnable run);
    /**
     * Delay in ticks
     */
    void runTaskLaterAsync(Runnable run, long delay);
    /**
     * Delay and Perion in ticks
     */
    void runTaskTimerAsync(Runnable run, long delay, long period);
    void runAtEntity(Entity entity, Runnable run);
    /**
     * Delay in ticks
     */
    void runAtEntityLater(Entity entity, Runnable run, long delay);
    /**
     * Delay and Perion in ticks
     */
    void runAtEntityTimer(Entity entity, Runnable run, long delay, long period);

}
