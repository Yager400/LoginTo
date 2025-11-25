package net.loginto.Events;

import static net.loginto.Configuration.LoggedPlayers.isPlayerLogged;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMove implements Listener {


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        
        if (!isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
