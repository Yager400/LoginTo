package net.loginto.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import static net.loginto.Configuration.LoggedPlayers.*;

public class InventoryPickupItem implements Listener {
    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        Player player = ((Player) event).getPlayer();
        if (!isPlayerLogged(player)) {
            event.setCancelled(true);
        }
    };
}
