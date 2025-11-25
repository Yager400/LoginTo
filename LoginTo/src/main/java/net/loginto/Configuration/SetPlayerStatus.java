package net.loginto.Configuration;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SetPlayerStatus {
    public static void unlockPlayer(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.BLINDNESS) {
                player.removePotionEffect(effect.getType());
                break;
            }
        }
    }

    public static void lockPlayer(Player player) {
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999999, 1), true);
    }
}
