/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;
import static net.loginto.bukkit.Configuration.Messages.getMessage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class onCommandEvent implements Listener {

    public final Plugin plugin;

    public onCommandEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String command = event.getMessage().split(" ")[0].substring(1);

        if (!isPlayerLogged(player)) {
            if (!command.equalsIgnoreCase("login") && !command.equalsIgnoreCase("register")) {
                event.setCancelled(true);
                player.sendMessage(getMessage("errors.oncommand_when_not_authenticated", plugin));
            }
        }
    }

}
