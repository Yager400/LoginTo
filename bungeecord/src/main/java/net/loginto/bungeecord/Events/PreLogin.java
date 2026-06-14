/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord.Events;

import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.PlayerUtils.Sessions;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.Files.MessageKeys;
import net.loginto.common.Utils.PremiumUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class PreLogin implements Listener {

    private final Database database;
    private final ProxyServer server;
    private final Logger logger;
    private final FloodgateApi floodgateApi;
    private final PremiumSQLiteCache sqliteCache;

    private final HashMap<String, String> temporaryLoginState = new HashMap<>();

    public PreLogin(Database database, ProxyServer server, Logger logger, PremiumSQLiteCache sqliteCache) {
        this.database = database;
        this.server = server;
        this.logger = logger;
        this.sqliteCache = sqliteCache;

        if (server.getPluginManager().getPlugin("floodgate") == null && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_PREMIUM_FEATURES.path())) {
            logger.warning("Floodgate is not installed, if a bedrock player joins the network, they will be kicked");
            floodgateApi = null;
        } else {
            floodgateApi = FloodgateApi.getInstance();
        }
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {

        for (ProxiedPlayer player : server.getPlayers()) {
            if (player.getName().equalsIgnoreCase(event.getConnection().getName())) {
                //event.getConnection().disconnect(LoginToFiles.Messages.getMessageComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_SAME_NAME.path()));
                event.setCancelReason(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.ERRORS_LOGIN_FAIL_ONKICK_SAME_NAME.path()));
                return;
            }
        }

        // in Sessions.addBorrowData, the ->noregistration mark is to tell if the player is already registered

        String username = event.getConnection().getName();

        if (!LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_PREMIUM_FEATURES.path())) {
            event.getConnection().setOnlineMode(false);
            if (database.isPlayerPresentInDB(username)) {
                temporaryLoginState.put(username, "cracked");
            } else {
                temporaryLoginState.put(username, "cracked->noregistration");
            }
            return;
        }

        UUID floodgateUUID = event.getConnection().getUniqueId();;
        if (floodgateApi != null && floodgateUUID != null) {
            if (floodgateApi.isFloodgateId(floodgateUUID)) {
                if (database.isPlayerPresentInDB(username)) {
                    temporaryLoginState.put(username, "bedrock");
                } else {
                    temporaryLoginState.put(username, "bedrock->noregistration");
                }
                return;
            }
        }

        boolean isUsernameValid = PremiumUtils.checkValidUsername(username);

        if (!isUsernameValid) {
            event.setCancelReason(LoginToFiles.Messages.getMessageLegacyComponent(MessageKeys.ERRORS_LOGIN_FAIL_INVALID_USERNAME.path()));
            return;
        }

        boolean isUsernamePremium = PremiumUtils.isUserNamePremium(username, sqliteCache);

        if (database.isPlayerPresentInDB(username)) {
            if (database.isPremium(username)) {
                event.getConnection().setOnlineMode(true);
                temporaryLoginState.put(username, "premium");
            } else {
                event.getConnection().setOnlineMode(false);
                temporaryLoginState.put(username, "cracked");
            }
        } else if (isUsernamePremium) {
            event.getConnection().setOnlineMode(true);
            temporaryLoginState.put(username, "premium->noregistration");
        } else {
            event.getConnection().setOnlineMode(false);
            temporaryLoginState.put(username, "cracked->noregistration");
        }
    }

    //Event for getting the UUID
    @EventHandler
    public void onLoginEvent(LoginEvent event) {

        String username = event.getConnection().getName();

        Sessions.addBorrowData(
                event.getConnection().getUniqueId(),
                temporaryLoginState.get(username)
        );
        temporaryLoginState.remove(username);

    }

}
