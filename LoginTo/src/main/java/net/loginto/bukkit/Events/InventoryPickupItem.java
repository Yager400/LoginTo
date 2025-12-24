/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;

public class InventoryPickupItem implements Listener {
    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        Player player = ((Player) event).getPlayer();
        if (!isPlayerLogged(player)) {
            event.setCancelled(true);
        }
    };
}
