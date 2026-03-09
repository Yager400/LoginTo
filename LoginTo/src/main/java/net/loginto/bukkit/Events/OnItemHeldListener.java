/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;

public class OnItemHeldListener implements Listener {

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        if (!isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}