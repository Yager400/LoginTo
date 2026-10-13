/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.playerutils.kyorihandlers;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.yager400.loginto.bukkit.LoginTo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Handler for using minimessage on spigot or paper < V1.18.2
 */
public class LegacyKyoriHandler implements KyoriHandler {

    public final MiniMessage miniMessage;

    public LegacyKyoriHandler() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    private String getLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public Component getMessageAsComponent(String message) {
        return this.miniMessage.deserialize(message);
    }

    @Override
    public void sendMessage(Player player, String message) {
        if (player == null) {
            return;
        }
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        if (user == null) {
            return;
        }
        Component component = this.getMessageAsComponent(message);
        if (user.getPacketVersion().isNewerThanOrEquals(ClientVersion.V_1_16)) {
            LoginTo.getAdventure().player(player).sendMessage(component);
        } else {
            player.sendMessage(this.getLegacyString(component));
        }
    }

    @Override
    public void sendConsole(String message) {
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
            LoginTo.getAdventure().console().sendMessage(getMessageAsComponent(message));
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(getLegacyString(getMessageAsComponent(message)));
        }
    }
}
