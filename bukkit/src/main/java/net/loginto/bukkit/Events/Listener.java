/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import net.loginto.bukkit.Events.Listeners.PacketEventListeners;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Premium.bukkit.PacketEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Listener {

    public static void implementPacketEventListener(Plugin plugin) {
        EventManager events = PacketEvents.getAPI().getEventManager();
        events.registerListener(new PacketEventListeners(), PacketListenerPriority.LOWEST);

        if (LoginToFiles.Experimental.getExperimentalBoolean("premium.bukkit-premium-auth", plugin)) {
            if (!Bukkit.getOnlineMode()) {
                events.registerListener(new PacketEventListener(plugin), PacketListenerPriority.HIGHEST);
                if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
                    plugin.getLogger().warning("Floodgate not detected, every bedrock player without a java account will get disconnected. To fix this, just install floodgate and geyser in your server (no special configuration needed).");
                }
            } else {
                plugin.getLogger().severe("Premium auth disabled because server is in online mode");
            }
        }
    }
}
