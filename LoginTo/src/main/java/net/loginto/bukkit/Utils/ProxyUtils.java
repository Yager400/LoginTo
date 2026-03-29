/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ProxyUtils {
    
    // This will not work if the player is not logged
    public static void sendPlayerToLobbyPostLogin(Plugin plugin, Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                String serverName = LoginToFiles.Config.getString("integrations.proxy.server-post-login", plugin);

                if (serverName == null || serverName.isEmpty()) return;

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                out.writeUTF("Connect");
                out.writeUTF(serverName);

                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20);
    }
}
