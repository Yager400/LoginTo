/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee;

import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.common.data.PremiumCache;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.files.WebhookKeys;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.bridge.DatabaseBridge;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.io.File;

public class LoginTo extends Plugin  {

    public static Plugin instance;
    public static Database database;
    public static BungeeAudiences adventure;
    public static YamlReader configReader;
    public static YamlReader messageReader;
    public static YamlReader webhookReader;
    public static PremiumCache premiumCache;
    public static DatabaseBridge databaseBridge;

    @Override
    public void onEnable() {
        instance = this;

        PluginSetup.saveFiles();
        PluginSetup.downloadDependencies();
        PluginSetup.UpdateUtils.checkForFileUpdates();

        configReader = PluginSetup.getYamlRead(new File(getDataFolder(), "config.yml"));
        messageReader = PluginSetup.getYamlRead(new File(getDataFolder(), "messages.yml"));
        webhookReader = PluginSetup.getYamlRead(new File(getDataFolder(), "webhooks.yml"));

        LoginTo.database = PluginSetup.initDatabase(configReader);

        PluginSetup.setWebhookUrl(webhookReader.getString(WebhookKeys.DISCORDWEBHOOKURL));

        //premiumCache = new PremiumCache();

        PluginSetup.PluginEventAndCommand.initializeCommands();
        PluginSetup.PluginEventAndCommand.initializeListeners();

        premiumCache = new PremiumCache(getDataFolder().toPath(), LoginTo.getConfigReader().getInt(ConfigKeys.SETTINGS_PREMIUM_CACHEDURATION));

        Metrics metrics = new Metrics(this, 31988);

        if (configReader.getBoolean(ConfigKeys.SETTINGS_CHECKFORUPDATES)) {
            PluginSetup.UpdateUtils.startUpdateCheckerCycle();
        }

        if (configReader.getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN).equals(configReader.getString(ConfigKeys.SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN))) {
            getLogger().warning("The post login and pre login servers are the same! This is optional, but it's better to use 1 lobby server and 1 auth server for better isolation.");
        }

        adventure = BungeeAudiences.create(this);

        if (configReader.getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
            try {
                databaseBridge = new DatabaseBridge(database, database.databaseType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        database.close();
        PremiumCache.closeIfOpen();
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }

    public static BungeeAudiences getAdventure() {
        return adventure;
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

