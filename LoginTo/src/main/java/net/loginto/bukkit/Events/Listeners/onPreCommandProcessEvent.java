/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.Utils.LoginToFiles;

public class onPreCommandProcessEvent implements Listener {

    private final Plugin plugin;

    public onPreCommandProcessEvent(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage().trim();

        if (!message.startsWith("/")) return;

        String command = message.substring(1).split(" ")[0];

        if (!Sessions.isPlayerLogged(player)) {

            for (Object allowedCommand : plugin.getConfig().getList("commands-settings.pre-login-allowed-commands")) {
                if (command.equals(String.valueOf(allowedCommand))) return;
            }

            if (
                !command.equals("login") &&
                !command.equals("register")
            ) {
                event.setCancelled(true);
                player.sendMessage(LoginToFiles.Messages.getMessage("errors.activity-before-login.oncommand-when-not-authenticated", player, plugin));
            }
        }
    }
}
