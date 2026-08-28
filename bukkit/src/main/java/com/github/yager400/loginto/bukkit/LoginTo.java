/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.common.data.files.WebhookKeys;
import com.github.yager400.loginto.common.data.PremiumCache;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.files.FilesManager;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.bridge.DatabaseBridge;
import com.github.yager400.loginto.folia.FoliaLib;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LoginTo extends JavaPlugin {

    public static Plugin instance;
    public static JavaPlugin javaPlugin;
    public static Database database;
    public static YamlReader configReader;
    public static YamlReader messageReader;
    public static YamlReader webhookReader;
    public static BukkitAudiences adventure;
    public static DatabaseBridge databaseBridge;

    @Override
    public void onLoad() {
        LoginTo.instance = this;
        LoginTo.javaPlugin = this;
        FoliaLib.init(this);
        PluginSetup.saveFiles();

        // Use the bukkit yaml reader, otherwise NoClassDefFoundError for SnakeYaml
        YamlConfiguration bukkitConfigReader = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        // Check for the old ConfigVersion path, it exists, install packetevents, otherwise check in the config (only new server)
        if (bukkitConfigReader.contains("ConfigVersion")) {
            try {
                FilesManager.deleteDirectory(new File(getDataFolder(), "lib").toPath()); // Delete the old library folder (with the new package com.github it breaks)
                new File(getDataFolder(), "rockyou.txt").delete(); // Also delete the old rockyou since it's over 130MB of space
            } catch (IOException e) {e.printStackTrace();}
            PluginSetup.downloadDependencies(true);
        } else {
            PluginSetup.downloadDependencies(bukkitConfigReader.getBoolean(ConfigKeys.SETTINGS_USEBUILTINPACKETEVENTS.value()));
        }
        PluginSetup.UpdateUtils.checkForFileUpdates();

        configReader = PluginSetup.getYamlRead(new File(getDataFolder(), "config.yml"));
        messageReader = PluginSetup.getYamlRead(new File(getDataFolder(), "messages.yml"));
        webhookReader = PluginSetup.getYamlRead(new File(getDataFolder(), "webhooks.yml"));

        LoginTo.database = PluginSetup.initDatabase(configReader);

        PluginSetup.setWebhookUrl(webhookReader.getString(WebhookKeys.DISCORDWEBHOOKURL));

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        // Run those things later, this is because on folia, it gives problems
         // initializing PacketEvents
        FoliaLib.get().runTaskLater(() -> {
            PacketEvents.getAPI().init();
            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
                try {
                    getLogger().warning("LoginTo is configured for working with the proxy, commands are not registered.");
                    databaseBridge = new DatabaseBridge(LoginTo.getDatabase(), database.databaseType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                getLogger().info("Initialized commands");
                PluginSetup.PluginEventAndCommand.initializeCommands();
            }
            getLogger().info("Initialized listeners");
            PluginSetup.PluginEventAndCommand.initializeListeners();
        }, 20L);

        adventure = BukkitAudiences.create(this);
        PluginSetup.UpdateUtils.startUpdateCheckerCycle();

        Metrics metrics = new Metrics(this, 28083);
        metrics.addCustomChart(new SimplePie("storage_type_used", () -> database.databaseType));
    }

    @Override
    public void onDisable() {
        database.close();
        PremiumCache.closeIfOpen();
        PacketEvents.getAPI().terminate();
    }

    public static Plugin getInstance() {
        return instance;
    }

    public static JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    public static Database getDatabase() {
        return database;
    }

    public static BukkitAudiences getAdventure() {
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

    public static DatabaseBridge getDatabaseBridge() {
        return databaseBridge;
    }
}
