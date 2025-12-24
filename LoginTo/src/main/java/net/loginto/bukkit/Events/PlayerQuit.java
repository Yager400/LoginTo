/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;
import static net.loginto.bukkit.ExtraFeature.Tries.removePlayerTries;

public class PlayerQuit implements Listener {

    private final Plugin plugin;

    public PlayerQuit(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isPlayerLogged(event.getPlayer())) {
            removePlayer(event.getPlayer());
        }
        removePlayerTries(event.getPlayer(), plugin);
    };
}
