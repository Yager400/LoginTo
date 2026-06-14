/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.PlayerUtils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.loginto.bukkit.LoginTo;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PlayerMessages {

    private static String mmLegacySerialized(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public static class player {
        public static void sendMessage(String path, Player player, Plugin plugin) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if (user.getPacketVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
                LoginTo.getAdventure().player(player).sendMessage(LoginToFiles.Messages.getMessage(path, player, plugin, null));
            } else {
                player.sendMessage(mmLegacySerialized(LoginToFiles.Messages.getMessage(path, player, plugin, null)));
            }
        }
        public static void sendMessage(String path, Player player, Plugin plugin, Map<String, String> placeholders) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
            if (user.getPacketVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
                LoginTo.getAdventure().player(player).sendMessage(LoginToFiles.Messages.getMessage(path, player, plugin, placeholders));
            } else {
                player.sendMessage(mmLegacySerialized(LoginToFiles.Messages.getMessage(path, player, plugin, placeholders)));
            }
        }
        public static void kickPlayer(String path, Player player, Plugin plugin) {
            player.kickPlayer(mmLegacySerialized(LoginToFiles.Messages.getMessage(path, player, plugin, null)));
        }
    }

    public static class console {
        public static void sendMessage(String path, Plugin plugin) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
                LoginTo.getAdventure().console().sendMessage(LoginToFiles.Messages.getMessage(path, plugin, null));
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(mmLegacySerialized(LoginToFiles.Messages.getMessage(path, plugin, null)));
            }
        }
        public static void sendMessage(String path, Plugin plugin, Map<String, String> placeholders) {
            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
                LoginTo.getAdventure().console().sendMessage(LoginToFiles.Messages.getMessage(path, plugin, placeholders));
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(mmLegacySerialized(LoginToFiles.Messages.getMessage(path, plugin, placeholders)));
            }
        }
    }
}
