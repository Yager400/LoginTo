/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;

import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.UUID;

public class PreLogin {

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {

        List<?> premiumAuthBypass = LoginTo.getConfigReader().getList(ConfigKeys.SETTINGS_PREMIUM_PREMIUMBYPASSLIST);
        // Obtain the player's uuid via mojang or by generating it since, at this point, we don't have the player's uuid
        final UUID playerUUID;
        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
            playerUUID = PlayerProtocolUtils.getUUIDFromName(event.getUsername(), premiumAuthBypass, LoginTo.getPremiumCache());
        } else {
            playerUUID = PlayerProtocolUtils.generateUUIDFromUsername(event.getUsername());
        }

        for (Player player : LoginTo.getServer().getAllPlayers()) {
            if (player.getUsername().equalsIgnoreCase(event.getUsername())) {
                event.setResult(
                        PreLoginEvent.PreLoginComponentResult.denied(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_ANOTHERPLAYERWITHSAMENAME)))
                );
                return;
            }
        }

        if (!LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, false, false)
            );
            return;
        }

        for (Object name : premiumAuthBypass) {
            if (name instanceof String nameString) {
                if (event.getUsername().equalsIgnoreCase(nameString)) {
                    event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
                    PlayerProtocolUtils.addAuthenticatedPlayer(
                            playerUUID,
                            new AuthenticatedPlayer(playerUUID, false, false)
                    );
                    return;
                }
            } else {
                LoginTo.getLogger().error("Skipped " + name + " because is not a string");
            }
        }

        if (LoginTo.getServer().getPluginManager().isLoaded("floodgate")) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(playerUUID)) {
                PlayerProtocolUtils.addAuthenticatedPlayer(
                        playerUUID,
                        new AuthenticatedPlayer(playerUUID, false, true)
                );
                return;
            }
        }

        int mojangResponse = PlayerProtocolUtils.getMojangAccountType(event.getUsername(), LoginTo.getPremiumCache());

        boolean databaseContainsPlayer = LoginTo.getDatabase().databaseContainsPlayer(playerUUID);
        // Treat player as cracked if not premium and either Mojang auth failed or player is already known as cracked in the database
        if (!LoginTo.getDatabase().isPremium(playerUUID) && (mojangResponse != 200
                || (LoginTo.getDatabase().isCracked(playerUUID) && databaseContainsPlayer))) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, false, false)
            );
        } else if (mojangResponse == 200) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, true, false)
            );
        } else {
            LoginTo.getLogger().error("Unable to authenticate this player, unknown error!");
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Messages.getKickMessage("<red>Unable to authenticate! Contact the network owner.")));
        }
    }
}
