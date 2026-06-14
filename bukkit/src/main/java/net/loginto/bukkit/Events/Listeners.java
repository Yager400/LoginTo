/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.loginto.bukkit.Events.Listener.*;
import net.loginto.bukkit.Database.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.PacketEventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Listeners {

    private static List<Listener> bukkitListeners = new ArrayList<>();

    private static PacketListener packetListener = null;
    private static PacketListener bukkitPremiumAuthListener = null;

    public static void registerAllListener(Plugin plugin, Database database) {
        implementPacketEventListener(plugin, database);

        Listener cancelledEvent = new CancelledEvents(plugin);
        bukkitListeners.add(cancelledEvent);
        plugin.getServer().getPluginManager().registerEvents(cancelledEvent, plugin);

        Listener joinEvent = new onJoinEvent(plugin, database);
        bukkitListeners.add(joinEvent);
        plugin.getServer().getPluginManager().registerEvents(joinEvent, plugin);

        Listener preCommandProcessEvent = new onPreCommandProcessEvent(plugin);
        bukkitListeners.add(preCommandProcessEvent);
        plugin.getServer().getPluginManager().registerEvents(preCommandProcessEvent, plugin);

        Listener quitEvent = new onQuitEvent(plugin);
        bukkitListeners.add(quitEvent);
        plugin.getServer().getPluginManager().registerEvents(quitEvent, plugin);

        Listener logAnotherLocEvents = new logAnotherLocEvents(plugin);
        bukkitListeners.add(logAnotherLocEvents);
        plugin.getServer().getPluginManager().registerEvents(logAnotherLocEvents, plugin);

        Listener changeWorldEvent = new OnPlayerChangeWorld(plugin);
        bukkitListeners.add(changeWorldEvent);
        plugin.getServer().getPluginManager().registerEvents(changeWorldEvent, plugin);
    }

    private static void implementPacketEventListener(Plugin plugin, Database database) {
        EventManager events = PacketEvents.getAPI().getEventManager();

        if (packetListener == null) {
            packetListener = new PacketEventListeners();
            events.registerListener(packetListener, PacketListenerPriority.LOWEST);
        }

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), plugin) && bukkitPremiumAuthListener == null) {
            if (!Bukkit.getOnlineMode()) {
                bukkitPremiumAuthListener = new PacketEventListener(plugin, database);
                events.registerListener(bukkitPremiumAuthListener, PacketListenerPriority.HIGHEST);
                if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
                    plugin.getLogger().warning("Floodgate not detected, every bedrock player without a java account will get disconnected. To fix this, just install floodgate and geyser in your server (no special configuration needed).");
                }
            } else {
                plugin.getLogger().severe("Premium auth disabled because server is in online mode");
            }
        }
    }

    public static void unregisterAllListeners() {
        for (Listener i : bukkitListeners) {
            HandlerList.unregisterAll(i);
        }
        bukkitListeners.clear();
    }
}
