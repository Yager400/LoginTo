/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity.Events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.common.Utils.PremiumUtils;
import org.geysermc.floodgate.api.FloodgateApi;
import org.slf4j.Logger;

import java.util.UUID;

public class PreLogin {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;
    private final FloodgateApi floodgateApi;
    private final PremiumSQLiteCache sqliteCache;

    public PreLogin(Database database, ProxyServer server, Logger logger, PremiumSQLiteCache sqliteCache) {
        this.database = database;
        this.server = server;
        this.logger = logger;
        this.sqliteCache = sqliteCache;

        if (!server.getPluginManager().isLoaded("floodgate") && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_PREMIUM_FEATURES.path())) {
            logger.warn("Floodgate is not installed, if a bedrock player joins the network, they will be kicked");
            floodgateApi = null;
        } else {
            floodgateApi = FloodgateApi.getInstance();
        }
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {

        for (Player player : server.getAllPlayers()) {
            if (player.getUsername().equalsIgnoreCase(event.getUsername())) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_SAME_NAME.path())));
                return;
            }
        }

        // in Sessions.addBorrowData, the ->noregistration mark is to tell if the player is already registered

        String username = event.getUsername();
        UUID uuid = event.getUniqueId();

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_PREMIUM_FEATURES.path())) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
            if (database.isPlayerPresentInDB(username)) {
                Sessions.addBorrowData(uuid, "cracked");
            } else {
                Sessions.addBorrowData(uuid, "cracked->noregistration");
            }
            return;
        }

        if (floodgateApi != null) {
            if (floodgateApi.isFloodgateId(uuid)) {
                if (database.isPlayerPresentInDB(username)) {
                    Sessions.addBorrowData(uuid, "bedrock");
                } else {
                    Sessions.addBorrowData(uuid, "bedrock->noregistration");
                }
                return;
            }
        }

        boolean isUsernameValid = PremiumUtils.checkValidUsername(username);

        if (!isUsernameValid) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_LOGIN_FAIL_INVALID_USERNAME.path())));
            return;
        }

        boolean isUsernamePremium = PremiumUtils.isUserNamePremium(username, sqliteCache);

        if (database.isPlayerPresentInDB(username)) {
            if (database.isPremium(username)) {
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
                Sessions.addBorrowData(uuid, "premium");
            } else {
                event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
                Sessions.addBorrowData(uuid, "cracked");
            }
        } else if (isUsernamePremium) {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOnlineMode());
            Sessions.addBorrowData(uuid, "premium->noregistration");
        } else {
            event.setResult(PreLoginEvent.PreLoginComponentResult.forceOfflineMode());
            Sessions.addBorrowData(uuid, "cracked->noregistration");
        }
    }

}
