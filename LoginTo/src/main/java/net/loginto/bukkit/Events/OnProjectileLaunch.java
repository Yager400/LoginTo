/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events;

import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;



public class OnProjectileLaunch implements Listener {
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!isPlayerLogged((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

}
