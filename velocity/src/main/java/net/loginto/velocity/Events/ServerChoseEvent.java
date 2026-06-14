/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.loginto.common.Database.Database;
import net.loginto.velocity.LoginTo;
import net.loginto.velocity.PlayerUtils.PlayerStatus;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;

import java.util.concurrent.TimeUnit;

public class ServerChoseEvent {

    private final Database database;
    private final ProxyServer server;
    private final LoginTo plugin;

    public ServerChoseEvent(Database database, ProxyServer server, LoginTo plugin) {
        this.database = database;
        this.server = server;
        this.plugin = plugin;
    }

    @Subscribe
    public void onPlayerChooseServer(PlayerChooseInitialServerEvent event) {
        String accState = Sessions.getAccState(event.getPlayer().getUniqueId());
        if (accState == null) {
            accState = "cracked";
        }
        if (database.canBypassLogin(event.getPlayer().getUsername())) {
            if (accState.equals("bedrock") || accState.equals("premium")) {
                PlayerStatus.setPlayerAsLogged(event, server);
            } else {
                PlayerStatus.setPlayerAsNotLogged(event, server);
            }
        } else {
            PlayerStatus.setPlayerAsNotLogged(event, server);
        }

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
    }

    protected void managePlayersPrompt(Player player, boolean isPremium, boolean isBedrock, boolean firstTime) {
        if (firstTime) {
            String watermark = (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_SHOW_WATERMARK.path())) ? " - Service offered by LoginTo on Modrinth" : "";
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS.path())) {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_PROMPT_CHARACTERS.path(), watermark));
            } else {
                player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.REGISTER_PROMPT.path(), watermark));
            }
            startCountdown(player);
            return;
        }

        if (isPremium) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_PREMIUM_LOGIN_SUCCESS.path()));
            return;
        }

        if (isBedrock) {
            player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_BEDROCK_LOGIN_SUCCESS.path()));
            return;
        }

        player.sendMessage(LoginToFiles.Messages.getMessageComponent(MessageKeys.LOGIN_PROMPT.path()));
        startCountdown(player);
    }

    private void startCountdown(Player player) {

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.AUTH_SECURITY_KICK_ON_AUTH_TIMEOUT.path())) {
            return;
        }

        server.getScheduler().buildTask(plugin, () -> {
            if (!Sessions.isPlayerLogged(player.getUniqueId())) {
                player.disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_LONG_WAITING.path()));
            }
        }).delay(LoginToFiles.Config.getInt(ConfigKeys.AUTH_SECURITY_AUTH_TIMEOUT_SECONDS.path()), TimeUnit.SECONDS).schedule();
    }
}
