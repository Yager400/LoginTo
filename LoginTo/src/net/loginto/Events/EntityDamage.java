package net.loginto.Events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static net.loginto.Configuration.LoggedPlayers.*;

public class EntityDamage implements Listener {
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            if (!isPlayerLogged(player)) {
                event.setCancelled(true);
            }
        }
        
    };
}
