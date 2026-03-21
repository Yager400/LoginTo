/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.Utils.LoginToFiles;

/*
Any event that will just be cancelled
 */

public class CancelledEvents implements Listener {

    private final Plugin plugin;
    
    public CancelledEvents(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (!Sessions.isPlayerLogged((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!Sessions.isPlayerLogged((Player)event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer())) {
            event.getPlayer().sendMessage(LoginToFiles.Messages.getMessage("errors.activity-before-login.chatting-before-login", event.getPlayer(), plugin));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!Sessions.isPlayerLogged((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onIntentoryDrag(InventoryDragEvent event) {
        if (!Sessions.isPlayerLogged((Player)event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

     @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Sessions.isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        
        if (!Sessions.isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Sessions.isPlayerLogged((Player)event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (!Sessions.isPlayerLogged((Player) event.getEntered())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!Sessions.isPlayerLogged(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!Sessions.isPlayerLogged(player)) {
            event.setCancelled(true);
        }
    }

    

}
