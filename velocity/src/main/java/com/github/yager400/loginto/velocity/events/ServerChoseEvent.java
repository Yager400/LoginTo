/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.events;

import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.SecurityUtils;
import com.github.yager400.loginto.velocity.LoginTo;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.github.yager400.loginto.velocity.playerutils.Messages;
import com.github.yager400.loginto.velocity.playerutils.PlayerStatus;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerChoseEvent {

    @Subscribe
    public void onServerChose(PlayerChooseInitialServerEvent event) {
        if (Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            return;
        }

        AuthenticatedPlayer authenticatedPlayer = PlayerProtocolUtils.getAuthenticatedPlayer(event.getPlayer().getUniqueId());
        if (authenticatedPlayer == null) {
            event.getPlayer().disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_SKIPPEDAUTHENTICATION)));
            return;
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {

            if (authenticatedPlayer.isBedrock) {
                Authentications.bedrockAuthentication(event.getPlayer(), event);
                return;
            }
            if (authenticatedPlayer.isPremium) {
                Authentications.premiumAuthentication(event.getPlayer(), authenticatedPlayer, event);
                return;
            }
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            if (LoginTo.getDatabase().databaseContainsPlayer(event.getPlayer().getUniqueId()) && !LoginTo.getDatabase().isSessionEndedOrInvalid(event.getPlayer().getUniqueId(), event.getPlayer().getRemoteAddress().getAddress().getHostAddress())) {
                PlayerStatus.setPlayerAsLoggedViaEvent(event);
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.SESSIONS_LOGGEDINWITHSESSION), event.getPlayer(), null, true);
                return;
            }
        }

        PlayerStatus.setPlayerAsNotLoggedViaEvent(event);
        Authentications.normalOrCrackedAuthentication(event.getPlayer());
    }

    protected static class Authentications {
        public static void premiumAuthentication(Player player, AuthenticatedPlayer authenticatedPlayer, PlayerChooseInitialServerEvent event) {
            Player authenticatedPlayerProfile = LoginTo.getServer().getPlayer(authenticatedPlayer.playerUUID).orElse(null);

            if (authenticatedPlayerProfile == null) {
                player.disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGGEDINWITHDIFFERENTUUID)));
                return;
            }
            boolean databaseContainsPlayer = LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId());

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                    && !databaseContainsPlayer) {
                JoinUtils.handleAutoRegistration(player, true, false, event);
                return;
            }

            if (!databaseContainsPlayer) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLoggedViaEvent(event);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINPREMIUM), player, null, true);
        }
        public static void bedrockAuthentication(Player player, PlayerChooseInitialServerEvent event) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

            if (floodgatePlayer == null) {
                player.disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_JAVAPLAYERMARKEDASBEDROCK)));
                return;
            }
            boolean databaseContainsPlayer = LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId());

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                    && !databaseContainsPlayer) {
                JoinUtils.handleAutoRegistration(player, false, true, event);
                return;
            }

            if (!databaseContainsPlayer) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLoggedViaEvent(event);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINBEDROCK), player, null, true);
        }
        public static void normalOrCrackedAuthentication(Player player) {
            Tries.addPlayer(player.getUniqueId());
            int timeout = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_AUTHENTICATIONTIMEOUT);
            if (timeout > 0) {
                LoginTo.getServer().getScheduler().buildTask(LoginTo.getInstance(), () -> {
                    if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                        player.disconnect(Messages.getKickMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORLONGWAITING)));
                    }
                }).delay(timeout, TimeUnit.SECONDS);
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.sendRegisterPrompt(player);
            } else {
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINPROMPT), player, null, true);
            }
        }
    }

    protected static class JoinUtils {
        public static void sendRegisterPrompt(Player player) {
            String message = (LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).isEmpty())
                    ? LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTERPROMPT)
                    : LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTERPROMPTREQUIRECHARACTERS);

            message += (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SHOWWATERMARK))
                    ? "<grey> - Service offered by LoginTo on Modrinth"
                    : "";

            if (!LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).isEmpty()) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%characters%", LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS));
                Messages.player.sendText(message, player, placeholders, true);
            } else {
                Messages.player.sendText(message, player, null, true);
            }
        }
        public static void handleAutoRegistration(Player player, boolean isPremium, boolean isBedrock, PlayerChooseInitialServerEvent event) {
            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                String password = SecurityUtils.PasswordSecurity.generatePassword();
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%autogenerated_password%", password);
                LoginTo.getDatabase().insertPlayer(player.getUniqueId(), password, "", isPremium, isBedrock, false, player.getRemoteAddress().getAddress().getHostAddress());
                PlayerStatus.setPlayerAsLoggedViaEvent(event);
                MessagesKeys autoRegisterKeyMSG = (isPremium)
                        ? MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERPREMIUM
                        : MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERBEDROCK;
                Messages.player.sendText(LoginTo.getMessageReader().getString(autoRegisterKeyMSG), player, placeholders, true);
            } else {
                LoginTo.getLogger().error("Executed auto registration, but the player is already registered!, player: " + player.getUsername());
            }
        }
    }

}
