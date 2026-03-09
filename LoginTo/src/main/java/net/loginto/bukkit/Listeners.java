/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.Events.*;

public class Listeners {
    public static void implementaListeners(JavaPlugin instance, DataBase database) {

        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(instance, database), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuit(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new OnChat(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerPickup(), instance);
        instance.getServer().getPluginManager().registerEvents(new EntityDamage(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnBlockBreak(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnPlayerMove(), instance);
        instance.getServer().getPluginManager().registerEvents(new onCommandEvent(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new InventoryOpen(), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerDropItem(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnInventoryClick(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnInventoryDrag(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnPlayerInteract(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnItemHeldListener(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnProjectileLaunch(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnVehicleEnter(), instance);

        EventManager events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new OnPacketSend(), PacketListenerPriority.HIGHEST);
        events.registerListener(new OnPacketReceived(), PacketListenerPriority.HIGHEST);
    }
}
