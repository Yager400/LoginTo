/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static net.loginto.bukkit.Configuration.LoggedPlayers.*;

public class OnBlockBreak implements Listener{



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
