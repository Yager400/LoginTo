/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.bungeecord.PlayerUtils.PasswordSecurity;
import net.loginto.bungeecord.PlayerUtils.PlayerStatus;
import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.Database.Database;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ServerChoseEvent implements Listener {

    private final Database database;
    private final ProxyServer server;
    private final Plugin plugin;

    public ServerChoseEvent(Database database, ProxyServer server, Plugin plugin) {
        this.database = database;
        this.server = server;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChooseServer(ServerConnectEvent event) {
        String accState = Sessions.getAccState(event.getPlayer().getUniqueId());
        if (accState == null) {
            //accState = "cracked";
            return;
        }
        if (database.canBypassLogin(event.getPlayer().getName())) {
            if (accState.equals("bedrock") || accState.equals("premium")) {
                PlayerStatus.setPlayerAsLogged(event, server);
            } else {
                PlayerStatus.setPlayerAsNotLogged(event, server);
            }
        } else {
            PlayerStatus.setPlayerAsNotLogged(event, server);
        }

        try {
            switch (accState) {
                case "premium":
                case "bedrock":
                    managePlayersPrompt(event.getPlayer(), accState.equals("premium"), accState.equals("bedrock"), false);
                    break;

                case "premium->noregistration":
                case "bedrock->noregistration":
                    managePlayersPrompt(event.getPlayer(), accState.equals("premium"), accState.equals("bedrock"), true);
                    break;

                case "cracked":
                    managePlayersPrompt(event.getPlayer(), false, false, false);
                    break;

                case "cracked->noregistration":
                    managePlayersPrompt(event.getPlayer(), false, false, true);
                    break;
            }

            Sessions.removeBorrowedData(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
            event.getPlayer().disconnect("Login Error");
        }
    }

    protected void managePlayersPrompt(ProxiedPlayer player, boolean isPremium, boolean isBedrock, boolean firstTime) throws Exception {
        if (firstTime) {

            if ((isPremium || isBedrock) && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_AUTO_REGISTER.path())) {
                String password = PasswordSecurity.generatePassword();
                database.insertPlayer(player.getName(), password);
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%password%", password);
                if (isPremium) {
                    player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_AUTO_REGISTER_PREMIUM.path(), placeholders));
                } else {
                    player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_AUTO_REGISTER_BEDROCK.path(), placeholders));
                }
                return;
            }

            String watermark = (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_SHOW_WATERMARK.path())) ? " - Service offered by LoginTo on Modrinth" : "";
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS.path())) {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_PROMPT_CHARACTERS.path(), watermark));
            } else {
                player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.REGISTER_PROMPT.path(), watermark));
            }
            startCountdown(player);
            return;
        }

        if (isPremium) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_PREMIUM_LOGIN_SUCCESS.path()));
            return;
        }

        if (isBedrock) {
            player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_BEDROCK_LOGIN_SUCCESS.path()));
            return;
        }

        player.sendMessage(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.LOGIN_PROMPT.path()));
        startCountdown(player);
    }

    private void startCountdown(ProxiedPlayer player) {

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_AUTH_TIMEOUT.path())) {
            return;
        }

        server.getScheduler().schedule(plugin, () -> {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                player.disconnect(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_LONG_WAITING.path()));
            }
        }, LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_AUTH_TIMEOUT_SECONDS.path()), TimeUnit.SECONDS);

    }
}
