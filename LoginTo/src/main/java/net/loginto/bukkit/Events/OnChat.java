/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.bukkit.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Configuration.Messages;

import static net.loginto.bukkit.Configuration.LoggedPlayers.isPlayerLogged;

public class OnChat implements Listener {

    private final Plugin plugin;

    public OnChat(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!isPlayerLogged(event.getPlayer())) {
            event.getPlayer().sendMessage(Messages.PAPIFormat(event.getPlayer(), Messages.getMessage("errors.onchat_before_login", plugin)));
            event.setCancelled(true);
        }
    }

}
