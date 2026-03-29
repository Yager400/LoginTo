/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.Utils.LoginToFiles;

public class logAnotherLocEvents implements Listener {

    private final Plugin plugin;

    public logAnotherLocEvents(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReason().contains("another location")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void AsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(event.getName())) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(LoginToFiles.Messages.getMessage("errors.login-fail.onkick-for-joining-with-same-name", p, plugin));
                break;
            }
        }
    }
}
