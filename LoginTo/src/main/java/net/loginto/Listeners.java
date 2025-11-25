package net.loginto;

import org.bukkit.plugin.java.JavaPlugin;

import net.loginto.DataBases.DataBase;
import net.loginto.Events.*;

public class Listeners {
    public static void implementaListeners(JavaPlugin instance, DataBase database) {

        

        instance.getServer().getPluginManager().registerEvents(new PlayerJoin(instance, database), instance);
        instance.getServer().getPluginManager().registerEvents(new PlayerQuit(), instance);
        instance.getServer().getPluginManager().registerEvents(new InventoryPickupItem(), instance);
        instance.getServer().getPluginManager().registerEvents(new EntityDamage(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnBlockBreak(), instance);
        instance.getServer().getPluginManager().registerEvents(new OnPlayerMove(), instance);

    }
}
