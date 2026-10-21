/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events.premium;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateUtils {

    protected static boolean bedrockHandler(PacketReceiveEvent event, Plugin plugin) {

        FloodgatePlayer bedrockPlayer = FloodgateUtils.getFloodgatePlayer(event.getChannel());

        if (bedrockPlayer != null && !bedrockPlayer.getXuid().isEmpty()) {
            plugin.getLogger().info("Bedrock");
            LoginTo.getFoliaLib().getScheduler().runNextTick(wrappedTask -> {
                PlayerProtocolUtils.addAuthenticatedPlayer(bedrockPlayer.getJavaUniqueId(), new AuthenticatedPlayer(
                        bedrockPlayer.getJavaUniqueId(),
                        false,
                        true
                ));
                LoginTo.getDatabase().updateBedrock(bedrockPlayer.getJavaUniqueId(), true);
            });
            return true;
        }
        return false;
    }

    protected static FloodgatePlayer getFloodgatePlayer(Object channel) {
        AttributeKey<FloodgatePlayer> floodgateAttribute = AttributeKey.valueOf("floodgate-player");

        return ((Channel) channel).attr(floodgateAttribute).get();
    }
}
