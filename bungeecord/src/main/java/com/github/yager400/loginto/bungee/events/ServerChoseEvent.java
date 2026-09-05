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
import com.github.yager400.loginto.bungee.playerutils.PlayerStatus;
import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.SecurityUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerChoseEvent implements Listener {

    @EventHandler
    public void onServerChose(ServerConnectEvent event) {

        if (Sessions.isPlayerLogged(event.getPlayer().getUniqueId())) {
            return;
        }

        AuthenticatedPlayer authenticatedPlayer = PlayerProtocolUtils.getAuthenticatedPlayer(event.getPlayer().getUniqueId());
        if (authenticatedPlayer == null) {
            event.getPlayer().disconnect(
                    TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_SKIPPEDAUTHENTICATION)))
            );
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
            if (LoginTo.getDatabase().databaseContainsPlayer(event.getPlayer().getUniqueId()) && !LoginTo.getDatabase().isSessionEndedOrInvalid(event.getPlayer().getUniqueId(), event.getPlayer().getAddress().getAddress().getHostAddress())) {
                PlayerStatus.setPlayerAsLoggedViaEvent(event);
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.SESSIONS_LOGGEDINWITHSESSION), event.getPlayer(), null, true);
                return;
            }
        }

        PlayerStatus.setPlayerAsNotLoggedViaEvent(event);
        Authentications.normalOrCrackedAuthentication(event.getPlayer());
    }

    protected static class Authentications {
        public static void premiumAuthentication(ProxiedPlayer player, AuthenticatedPlayer authenticatedPlayer, ServerConnectEvent event) {
            ProxiedPlayer authenticatedPlayerProfile = LoginTo.getInstance().getProxy().getPlayer(authenticatedPlayer.playerUUID);

            if (authenticatedPlayerProfile == null) {
                player.disconnect(TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGGEDINWITHDIFFERENTUUID), null)));
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                    && !LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.handleAutoRegistration(player, true, false, event);
                return;
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLoggedViaEvent(event);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINPREMIUM), player, null, true);
        }
        public static void bedrockAuthentication(ProxiedPlayer player, ServerConnectEvent event) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

            if (floodgatePlayer == null) {
                player.disconnect(TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_JAVAPLAYERMARKEDASBEDROCK), null)));
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                    && !LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.handleAutoRegistration(player, false, true, event);
                return;
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLoggedViaEvent(event);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINBEDROCK), player, null, true);
        }
        public static void normalOrCrackedAuthentication(ProxiedPlayer player) {
            Tries.addPlayer(player.getUniqueId());
            int timeout = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_AUTHENTICATIONTIMEOUT);
            if (timeout > 0) {
                LoginTo.getInstance().getProxy().getScheduler().schedule(LoginTo.getInstance(), () -> {
                    if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                        player.disconnect(TextComponent.fromLegacyText(Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORLONGWAITING), null)));
                    }
                }, timeout, TimeUnit.SECONDS);
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.sendRegisterPrompt(player);
            } else {
                LoginTo.getInstance().getProxy().getScheduler().schedule(LoginTo.getInstance(), () -> {
                    if (Sessions.isPlayerLogged(player.getUniqueId())) {
                        return;
                    }
                    Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINPROMPT), player, null, false);
                }, 1, 4, TimeUnit.SECONDS);
            }
        }
    }

    protected static class JoinUtils {
        public static void sendRegisterPrompt(ProxiedPlayer player) {
            String message = (LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).isEmpty())
                    ? LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTERPROMPT)
                    : LoginTo.getMessageReader().getString(MessagesKeys.REGISTER_REGISTERPROMPTREQUIRECHARACTERS);

            message += (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SHOWWATERMARK))
                    ? "<grey> - Service offered by LoginTo on Modrinth"
                    : "";

            final String finalMessage = message;
            LoginTo.getInstance().getProxy().getScheduler().schedule(LoginTo.getInstance(), () -> {
                if (Sessions.isPlayerLogged(player.getUniqueId())) {
                    return;
                }
                if (!LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).isEmpty()) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%characters%", LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS));
                    Messages.player.sendText(finalMessage, player, placeholders, false);
                } else {
                    Messages.player.sendText(finalMessage, player, null, false);
                }
            }, 1, 4, TimeUnit.SECONDS);
        }
        public static void handleAutoRegistration(ProxiedPlayer player, boolean isPremium, boolean isBedrock, ServerConnectEvent event) {
            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                String password = SecurityUtils.PasswordSecurity.generatePassword();
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%autogenerated_password%", password);
                LoginTo.getDatabase().insertPlayer(player.getUniqueId(), password, "", isPremium, isBedrock, false, player.getAddress().getAddress().getHostAddress());
                PlayerStatus.setPlayerAsLoggedViaEvent(event);
                MessagesKeys autoRegisterKeyMSG = (isPremium)
                        ? MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERPREMIUM
                        : MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERBEDROCK;
                Messages.player.sendText(LoginTo.getMessageReader().getString(autoRegisterKeyMSG), player, placeholders, true);
            } else {
                LoginTo.getInstance().getLogger().severe("Executed auto registration, but the player is already registered!, player: " + player.getName());
            }
        }
    }

}
