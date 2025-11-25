package net.loginto.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static net.loginto.Configuration.LoggedPlayers.*;

public class OnBlockBreak implements Listener{



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isPlayerLogged(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
