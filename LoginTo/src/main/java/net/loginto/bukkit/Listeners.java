/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.bukkit.DataBases.DataBase;
import net.loginto.bukkit.Events.*;

public class Listeners {
    public static void implementaListeners(JavaPlugin instance, DataBase database) {

        

        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(instance, database), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuit(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new InventoryPickupItem(), instance);
        instance.getServer().getPluginManager().registerEvents(new EntityDamage(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnBlockBreak(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnPlayerMove(), instance);
        instance.getServer().getPluginManager().registerEvents(new onCommandEvent(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new InventoryOpen(), instance);

    }
}
