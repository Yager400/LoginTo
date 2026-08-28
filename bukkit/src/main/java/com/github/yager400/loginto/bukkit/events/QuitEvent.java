/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.playerutils.PlayerStatus;
import com.github.yager400.loginto.folia.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Save the last player's location
        // Save it only there, otherwise it will be overridden by the onJoin listener
        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SPAWNSETTING_ENABLED)) {
            Location playerLocation = player.getLocation();

            LoginTo.getDatabase().updateLastLocation(
                    player.getUniqueId(),
                    playerLocation.getWorld().getName(),
                    playerLocation.getX(),
                    playerLocation.getY(),
                    playerLocation.getZ()
            );
        }

        PlayerStatus.setPlayerAsNotLogged(player);

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            LoginTo.getDatabase().updateSession(
                    playerUUID,
                    event.getPlayer().getAddress().getAddress().getHostAddress(),
                    LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_SESSIONS_SESSIONDURATION) * 60 * 60
            );
        }
    }

}
