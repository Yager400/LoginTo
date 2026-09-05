/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.events;

import com.github.yager400.loginto.bukkit.LoginTo;
import com.github.yager400.loginto.bukkit.events.premium.ProtocolUtils;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.bukkit.playerutils.Messages;
import com.github.yager400.loginto.bukkit.playerutils.PlayerStatus;
import com.github.yager400.loginto.common.players.AuthenticatedPlayer;
import com.github.yager400.loginto.common.players.PlayerProtocolUtils;
import com.github.yager400.loginto.common.players.Sessions;
import com.github.yager400.loginto.common.players.Tries;
import com.github.yager400.loginto.common.utils.SecurityUtils;
import com.github.yager400.loginto.folia.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            this.handleProxyBukkitBridgeJoin(event);
            return;
        }

        PlayerStatus.setPlayerAsNotLogged(event.getPlayer());

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)
            && !Bukkit.getOnlineMode() /* It's better to do this check since the premium auth will still be active on online server */) {
            AuthenticatedPlayer authenticatedPlayer = PlayerProtocolUtils.getAuthenticatedPlayer(event.getPlayer().getUniqueId());

            if (authenticatedPlayer == null) {
                FoliaLib.get().kickPlayer(event.getPlayer(), Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_SKIPPEDAUTHENTICATION), event.getPlayer(), null));
                return;
            }

            if (authenticatedPlayer.isBedrock) {
                Authentications.bedrockAuthentication(event.getPlayer());
                return;
            }
            if (authenticatedPlayer.isPremium) {
                Authentications.premiumAuthentication(event.getPlayer(), authenticatedPlayer);
                return;
            }
        }

        if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_SESSIONS_ENABLED)) {
            if (LoginTo.getDatabase().databaseContainsPlayer(event.getPlayer().getUniqueId()) && !LoginTo.getDatabase().isSessionEndedOrInvalid(event.getPlayer().getUniqueId(), event.getPlayer().getAddress().getAddress().getHostAddress())) {
                PlayerStatus.setPlayerAsLogged(event.getPlayer());
                Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.SESSIONS_LOGGEDINWITHSESSION), event.getPlayer(), null);
                return;
            }
        }

        Authentications.normalOrCrackedAuthentication(event.getPlayer());
    }

    protected static class Authentications {
        public static void premiumAuthentication(Player player, AuthenticatedPlayer authenticatedPlayer) {
            Player authenticatedPlayerProfile = Bukkit.getPlayer(authenticatedPlayer.playerUUID);

            if (authenticatedPlayerProfile == null) {
                FoliaLib.get().kickPlayer(player, Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGGEDINWITHDIFFERENTUUID), player, null));
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                && !LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.handleAutoRegistration(player, true, false);
                return;
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLogged(player);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINPREMIUM), player, null);
        }
        public static void bedrockAuthentication(Player player) {
            FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());

            if (floodgatePlayer == null) {
                FoliaLib.get().kickPlayer(player, Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_JAVAPLAYERMARKEDASBEDROCK), player, null));
                return;
            }

            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_AUTOREGISTER)
                && !LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.handleAutoRegistration(player, false, true);
                return;
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                Authentications.normalOrCrackedAuthentication(player);
                return;
            }

            PlayerStatus.setPlayerAsLogged(player);
            Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.PREMIUM_LOGIN_AUTOLOGINBEDROCK), player, null);
        }
        public static void normalOrCrackedAuthentication(Player player) {
            Tries.addPlayer(player.getUniqueId());
            int timeout = LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PASSWORD_AUTHENTICATIONTIMEOUT);
            if (timeout > 0) {
                FoliaLib.get().runTaskLater(() -> {
                    if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                        FoliaLib.get().kickPlayer(player, Messages.getLegacyFormattedMessage(LoginTo.getMessageReader().getString(MessagesKeys.DURINGLOGIN_KICKEDFORLONGWAITING), player, null));
                    }
                }, timeout * 20L);
            }

            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                JoinUtils.sendRegisterPrompt(player);
            } else {
                FoliaLib.get().runTaskTimer(() -> {
                    if (Sessions.isPlayerLogged(player.getUniqueId())) {
                        return;
                    }
                    Messages.player.sendText(LoginTo.getMessageReader().getString(MessagesKeys.LOGIN_LOGINPROMPT), player, null);
                }, 20, 80);
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

            final String finalMessage = message;
            FoliaLib.get().runTaskTimer(() -> {
                if (Sessions.isPlayerLogged(player.getUniqueId())) {
                    return;
                }
                if (!LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS).isEmpty()) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("%characters%", LoginTo.getConfigReader().getString(ConfigKeys.SETTINGS_PASSWORD_REQUIREDCHARACTERS));
                    Messages.player.sendText(finalMessage, player, placeholders);
                } else {
                    Messages.player.sendText(finalMessage, player, null);
                }
            }, 20, 80);
        }
        public static void handleAutoRegistration(Player player, boolean isPremium, boolean isBedrock) {
            if (!LoginTo.getDatabase().databaseContainsPlayer(player.getUniqueId())) {
                String password = SecurityUtils.PasswordSecurity.generatePassword();
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%autogenerated_password%", password);
                LoginTo.getDatabase().insertPlayer(player.getUniqueId(), password, "", isPremium, isBedrock, false, player.getAddress().getAddress().getHostAddress());
                PlayerStatus.setPlayerAsLogged(player);
                MessagesKeys autoRegisterKeyMSG = (isPremium)
                        ? MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERPREMIUM
                        : MessagesKeys.PREMIUM_REGISTRATION_AUTOREGISTERBEDROCK;
                Messages.player.sendText(LoginTo.getMessageReader().getString(autoRegisterKeyMSG), player, placeholders);
            } else {
                LoginTo.getInstance().getLogger().severe("Executed auto registration, but the player is already registered!, player: " + player.getName());
            }
        }
    }

    public void handleProxyBukkitBridgeJoin(PlayerJoinEvent event) {
        if (LoginTo.getDatabaseBridge() != null && LoginTo.getDatabaseBridge().isPlayerLogged(event.getPlayer().getUniqueId())) {
            PlayerStatus.setPlayerAsLogged(event.getPlayer());
        } else {
            PlayerStatus.setPlayerAsNotLogged(event.getPlayer());
            CompletableFuture.runAsync(() -> {
                while (LoginTo.getDatabaseBridge() != null && !LoginTo.getDatabaseBridge().isPlayerLogged(event.getPlayer().getUniqueId())) {
                    // If the player got kicked in the server
                    if (Bukkit.getPlayer(event.getPlayer().getUniqueId()) == null) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                FoliaLib.get().runAtEntity(event.getPlayer(), () -> {
                    PlayerStatus.setPlayerAsLogged(event.getPlayer());
                });
            });
        }
    }

}
