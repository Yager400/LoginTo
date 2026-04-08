/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;

import net.loginto.velocity.Database.Database;

public class CommandEvent {

    public final Database database;

    public CommandEvent(Database database) {
        this.database = database;
    }

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {

        if (
            event.getCommand().startsWith("login") || 
            event.getCommand().startsWith("l") ||
            event.getCommand().startsWith("register") || 
            event.getCommand().startsWith("r")
        ) return;

        if (event.getCommandSource() instanceof Player) {
            Player player = (Player) event.getCommandSource();
            if (!database.isPlayerLogged(player.getUsername())) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                System.out.println(event.getCommand());
            }
        }
    }
}
