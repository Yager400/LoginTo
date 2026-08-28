/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.events;

import com.github.yager400.loginto.bungee.LoginTo;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bungee.playerutils.Messages;
import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PreLogin implements Listener {

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {

        List<?> premiumAuthBypass = LoginTo.getConfigReader().getList(ConfigKeys.SETTINGS_PREMIUM_PREMIUMBYPASSLIST);
        // Obtain the player's uuid via mojang or by generating it since, at this point, we don't have the player's uuid
        final UUID playerUUID;
        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
            playerUUID = PlayerProtocolUtils.getUUIDFromName(event.getConnection().getName(), premiumAuthBypass, LoginTo.getPremiumCache());
        } else {
            playerUUID = PlayerProtocolUtils.generateUUIDFromUsername(event.getConnection().getName());
        }

        for (ProxiedPlayer player : LoginTo.getInstance().getProxy().getPlayers()) {
            if (player.getName().equalsIgnoreCase(event.getConnection().getName())) {
                event.setCancelReason(
                        TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_ANOTHERPLAYERWITHSAMENAME)))
                );
                return;
            }
        }

        if (!LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
            event.getConnection().setOnlineMode(false);
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, false, false)
            );
            return;
        }

        for (Object name : premiumAuthBypass) {
            if (name instanceof String nameString) {
                if (event.getConnection().getName().equalsIgnoreCase(nameString)) {
                    event.getConnection().setOnlineMode(false);
                    PlayerProtocolUtils.addAuthenticatedPlayer(
                            playerUUID,
                            new AuthenticatedPlayer(playerUUID, false, false)
                    );
                    return;
                }
            } else {
                LoginTo.getInstance().getLogger().severe("Skipped " + name + " because is not a string");
            }
        }

        if (LoginTo.getInstance().getProxy().getPluginManager().getPlugin("floodgate") != null) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(playerUUID)) {
                PlayerProtocolUtils.addAuthenticatedPlayer(
                        playerUUID,
                        new AuthenticatedPlayer(playerUUID, false, true)
                );
                return;
            }
        }

        int mojangResponse = PlayerProtocolUtils.getMojangAccountType(event.getConnection().getName(), LoginTo.getPremiumCache());

        boolean databaseContainsPlayer = LoginTo.getDatabase().databaseContainsPlayer(playerUUID);
        // Treat player as cracked if not premium and either Mojang auth failed or player is already known as cracked in the database
        if (!LoginTo.getDatabase().isPremium(playerUUID) && (mojangResponse != 200
                || (LoginTo.getDatabase().isCracked(playerUUID) && databaseContainsPlayer))) {
            event.getConnection().setOnlineMode(false);
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, false, false)
            );
        } else if (mojangResponse == 200) {
            event.getConnection().setOnlineMode(true);
            PlayerProtocolUtils.addAuthenticatedPlayer(
                    playerUUID,
                    new AuthenticatedPlayer(playerUUID, true, false)
            );
        } else {
            LoginTo.getInstance().getLogger().severe("Unable to authenticate this player, unknown error!");
            event.getConnection().disconnect();
        }
    }
}
