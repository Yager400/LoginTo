/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Events.Listener;

import net.loginto.bukkit.PlayerUtils.Positions;
import net.loginto.bukkit.PlayerUtils.Sessions;
import net.loginto.bukkit.PlayerUtils.Tries;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Objects;

public class onQuitEvent implements Listener {

    private final Plugin plugin;

    public onQuitEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        plugin.reloadConfig();

        if (Sessions.isPlayerLogged(player)) {
            Sessions.removePlayer(player);
            ;
        }
        Tries.resetTries(player);

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        if (player.getWorld().getName().equals(player.getName() + "-qrcode")) {

            World qrWorld = Bukkit.getWorld(player.getName() + "-qrcode");

            player.teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());

            if (qrWorld != null) {
                Bukkit.unloadWorld(qrWorld, false);
            }

            File worldFolder = new File(Bukkit.getWorldContainer(), player.getName() + "-qrcode");

            deleteFolder(worldFolder);
        }

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.SPAWN_SETTINGS_TELEPORT_ON_JOIN.path(), plugin)) {
            Location playerLocation = player.getLocation();

            Positions.setPlayerPosition(
                    player,
                    playerLocation.getWorld(),
                    playerLocation.getX(),
                    playerLocation.getY(),
                    playerLocation.getZ(),
                    plugin
            );

            World world = Bukkit.getWorld(LoginToFiles.Config.getString(ConfigKeys.SPAWN_SETTINGS_TARGET_DIMENSION.path(), plugin));
            double x = LoginToFiles.Config.getDouble(ConfigKeys.SPAWN_SETTINGS_SPAWN_COORDINATES_X.path(), plugin);
            double y = LoginToFiles.Config.getDouble(ConfigKeys.SPAWN_SETTINGS_SPAWN_COORDINATES_Y.path(), plugin);
            double z = LoginToFiles.Config.getDouble(ConfigKeys.SPAWN_SETTINGS_SPAWN_COORDINATES_Z.path(), plugin);

            Location location = new Location(world, x, y, z);

            player.teleport(location);
        }
    }

    private static void deleteFolder(File file) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                deleteFolder(f);
            }
        }
        file.delete();
    }
}
