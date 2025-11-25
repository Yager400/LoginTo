package net.loginto.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.loginto.Configuration.LoggedPlayers.*;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isPlayerLogged(event.getPlayer())) {
            removePlayer(event.getPlayer());
        }
    };
}
