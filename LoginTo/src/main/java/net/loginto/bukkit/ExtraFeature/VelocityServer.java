/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
/*

package net.loginto.bukkit.ExtraFeature;

import static net.loginto.bukkit.Configuration.Config.getStringFromConfig;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VelocityServer {

    public static void sendPlayerToServer(Player player, Plugin plugin) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            out.writeUTF("Connect");
            out.writeUTF(getStringFromConfig("proxy-integration.server_name", plugin));

            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
 */