/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity;

import com.github.yager400.loginto.common.data.PremiumCache;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.files.WebhookKeys;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.bridge.DatabaseBridge;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.velocitypowered.api.plugin.PluginContainer;
import net.byteflux.libby.VelocityLibraryManager;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.nio.file.Path;


public class LoginTo {

    private static LoginTo instance;
    private static ProxyServer server;
    private static Logger logger;
    private static Path dataDirectory;
    private static PluginContainer container;
    private final Metrics.Factory makeFactory;
    public static Database database;
    public static YamlReader configReader;
    public static YamlReader messageReader;
    public static YamlReader webhookReader;
    public static PremiumCache premiumCache;
    public static DatabaseBridge databaseBridge;

    @Inject
    public LoginTo(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory makeFactory, PluginContainer container) {
        LoginTo.server = server;
        LoginTo.logger = logger;
        LoginTo.dataDirectory = dataDirectory;
        LoginTo.instance = this;
        LoginTo.container = container;
        this.makeFactory = makeFactory;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {

        PluginSetup.saveFiles();
        PluginSetup.downloadDependencies(new VelocityLibraryManager<>(
                logger,
                dataDirectory,
                server.getPluginManager(),
                this
        ));
        PluginSetup.UpdateUtils.checkForFileUpdates();

        configReader = PluginSetup.getYamlRead(new File(dataDirectory.toFile(), "config.yml"));
        messageReader = PluginSetup.getYamlRead(new File(dataDirectory.toFile(), "messages.yml"));
        webhookReader = PluginSetup.getYamlRead(new File(dataDirectory.toFile(), "webhooks.yml"));

        LoginTo.database = PluginSetup.initDatabase(configReader);

        PluginSetup.setWebhookUrl(webhookReader.getString(WebhookKeys.DISCORDWEBHOOKURL));

        PluginSetup.PluginEventAndCommand.initializeCommands();
        PluginSetup.PluginEventAndCommand.initializeListeners();

        premiumCache = new PremiumCache(dataDirectory, LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PREMIUM_CACHEDURATION));

        Metrics metrics = makeFactory.make(this, 31987);

        if (configReader.getBoolean(ConfigKeys.SETTINGS_CHECKFORUPDATES)) {
            PluginSetup.UpdateUtils.startUpdateCheckerCycle();
        }

        if (configReader.getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN).equals(configReader.getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN))) {
            logger.warn("The post login and pre login servers are the same! This is optional, but it's better to use 1 lobby server and 1 auth server for better isolation.");
        }

        if (configReader.getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            try {
                databaseBridge = new DatabaseBridge(database, database.databaseType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        database.close();
        PremiumCache.closeIfOpen();
    }

    public static LoginTo getInstance() {
        return instance;
    }

    public static ProxyServer getServer() {
        return server;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static PluginContainer getContainer() {
        return container;
    }

    public static Database getDatabase() {
        return database;
    }

    public static YamlReader getConfigReader() {
        return configReader;
    }

    public static YamlReader getMessageReader() {
        return messageReader;
    }

    public static YamlReader getWebhookReader() {
        return webhookReader;
    }

    public static PremiumCache getPremiumCache() {
        return premiumCache;
    }

    public static DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }
}

