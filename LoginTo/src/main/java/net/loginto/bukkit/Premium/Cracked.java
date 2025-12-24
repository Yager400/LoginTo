/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Premium;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import static net.loginto.bukkit.Configuration.Messages.getMessage;
import static net.loginto.bukkit.Premium.Check.*;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Cracked {
    
    private static List<Player> commandExecutedPlayerList = new ArrayList<>();
    public static void executeCrackedCommand(Player player, Plugin plugin) {
        if (!commandExecutedPlayerList.contains(player)) {
            
            if (IsPlayerInThePremiumDB(player, plugin)) {
                player.sendMessage(getMessage("premium.cracked.already_cracked", plugin));
                return;
            }
            
            
            commandExecutedPlayerList.add(player);

            player.sendMessage(getMessage("premium.cracked.cracked_warn", plugin));
        } else {
            sendCrackedPluginMessage(player, plugin);
            player.sendMessage(getMessage("premium.cracked.cracked_done", plugin));
            commandExecutedPlayerList.remove(player);
        }
    }

    public static void sendCrackedPluginMessage(Player player, Plugin plugin) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("AddPlayersInfo");
            out.writeUTF(player.getName());
            out.writeUTF("false");
            
            player.sendPluginMessage(plugin, "loginto:authchannel", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
