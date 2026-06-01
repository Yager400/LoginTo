/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Premium.bukkit;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.util.crypto.SignatureData;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateUtils {

    protected static boolean bedrockHandler(PacketReceiveEvent event, Plugin plugin) {

        FloodgatePlayer bedrockPlayer = FloodgateUtils.getFloodgatePlayer(event.getChannel());

        if (bedrockPlayer != null && !bedrockPlayer.getXuid().isEmpty()) {
            plugin.getLogger().info("Bedrock");
            Bukkit.getScheduler().runTask(plugin, () -> {
                ProtocolUtils.authenticatedPlayer.put(bedrockPlayer.getJavaUniqueId(), new AuthenticatedPlayer(
                        bedrockPlayer.getJavaUniqueId(),
                        false,
                        true
                ));
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
