/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listener;

import net.loginto.bukkit.PlayerUtils.PlayerMessages;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

public class OnPlayerChangeWorld implements Listener {

    private final Plugin plugin;

    public OnPlayerChangeWorld(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().contains("qrcode")) {
            if (!player.getWorld().getName().contains(player.getName())) {
                Location loc = event.getFrom().getSpawnLocation();
                player.teleport(loc);
                PlayerMessages.player.sendMessage(MessageKeys.CHANGEPASSWORD_ERROR_QRCODE_WORLD_ACCESS_DENIED.path(), player, plugin);
            }
        }
    }
}
