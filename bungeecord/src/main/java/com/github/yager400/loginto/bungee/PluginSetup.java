/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee;

import com.github.yager400.loginto.bungee.commands.*;
import com.github.yager400.loginto.bungee.events.*;
import com.github.yager400.loginto.bungee.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bungee.fileskeys.MessagesKeys;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.database.connectors.MySQLConnector;
import com.github.yager400.loginto.common.data.database.connectors.SQLiteConnector;
import com.github.yager400.loginto.common.data.dependencies.CommonLibraries;
import com.github.yager400.loginto.common.data.files.FilesManager;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.utils.Updates;
import com.github.yager400.loginto.common.utils.WebHooks;
import net.byteflux.libby.BungeeLibraryManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PluginSetup {

    private static class DatabaseInit {
        public static Database initMySQLDatabase(String host, int port, String username, String password, String database) {

            return new Database(
                    MySQLConnector.getConfig(host, port, username, password, database ),
                    null,
                    "mysql"
            );

        }
        public static Database initSQLiteDatabase() {

            return new Database(
                    SQLiteConnector.getConfig(LoginTo.getInstance().getDataFolder()),
                    Paths.get(LoginTo.getInstance().getDataFolder().getAbsolutePath()),
                    "sqlite"
            );

        }
    }

    public static Database initDatabase(YamlReader reader) {
        return (reader.getString(ConfigKeys.DATASTORE_DATABASETYPE).equalsIgnoreCase("sqlite"))
                ? PluginSetup.DatabaseInit.initSQLiteDatabase()
                : PluginSetup.DatabaseInit.initMySQLDatabase(
                reader.getString(ConfigKeys.DATASTORE_HOST),
                reader.getInt(ConfigKeys.DATASTORE_PORT),
                reader.getString(ConfigKeys.DATASTORE_USER),
                reader.getString(ConfigKeys.DATASTORE_PASSWORD),
                reader.getString(ConfigKeys.DATASTORE_DATABASE)
        );
    }

    public static void saveFiles() {

        Plugin plugin = LoginTo.getInstance();

        FilesManager.makePluginDataFolder(plugin.getDataFolder().toPath());
        FilesManager.downloadRockYou(plugin.getDataFolder().toPath());

        Map<String, Path> files = new HashMap<>();
        files.put("proxy-config.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "config.yml"));
        files.put("proxy-messages.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "messages.yml"));
        files.put("webhooks.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "webhooks.yml"));

        FilesManager.saveFiles(files, false);
    }

    public static void downloadDependencies() {

        Plugin plugin = LoginTo.getInstance();

        plugin.getLogger().warning("If you get any exception due to a missing, broken or wrong library, delete the folder /lib into the LoginTo folder.");

        CommonLibraries.downloadLibraries(new BungeeLibraryManager(plugin));

    }

    public static class PluginEventAndCommand {
        public static void initializeCommands() {
            Plugin plugin = LoginTo.getInstance();
            plugin.getProxy().getPluginManager().registerCommand(plugin, new RegisterCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new LoginCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new UnregisterCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new ChangePasswordCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new PremiumCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new CrackedCommand());
            plugin.getProxy().getPluginManager().registerCommand(plugin, new LoginToCommand());
        }

        public static void initializeListeners() {
            Plugin plugin = LoginTo.getInstance();
            plugin.getProxy().getPluginManager().registerListener(plugin, new CancelledEvents());
            plugin.getProxy().getPluginManager().registerListener(plugin, new CommandEvent());
            plugin.getProxy().getPluginManager().registerListener(plugin, new DisconnectEvent());
            plugin.getProxy().getPluginManager().registerListener(plugin, new PermissionEvent());
            plugin.getProxy().getPluginManager().registerListener(plugin, new PLMessageEvent());
            plugin.getProxy().getPluginManager().registerListener(plugin, new PreLogin());
            plugin.getProxy().getPluginManager().registerListener(plugin, new ServerChoseEvent());

            if (LoginTo.getInstance().getProxy().getPluginManager().getPlugin("floodgate") == null) {
                LoginTo.getInstance().getLogger().warning("No floodgate detected! If a bedrock player joins the server, they will be kicked.");
            }
        }
    }

    public static YamlReader getYamlRead(File file) {
        return new YamlReader(file);
    }

    public static void setWebhookUrl(String url) {
        WebHooks.setWebHookUrl(url);
    }

    public static class UpdateUtils {
        public static void startUpdateCheckerCycle() {
            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_CHECKFORUPDATES)) {
                LoginTo.getInstance().getProxy().getScheduler().schedule(LoginTo.getInstance(), () -> {
                    if (Updates.isThereAnUpdate(LoginTo.getInstance().getDescription().getVersion())) {
                        LoginTo.getInstance().getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                    }
                }, 1, TimeUnit.HOURS);
            }
        }

        public static void checkForFileUpdates() {
            try {
                FilesManager.updateYamlFile(
                        new File(LoginTo.getInstance().getDataFolder(), "config.yml"),
                        "proxy-config.yml",
                        ConfigKeys.CONFIGVERSION
                );
                FilesManager.updateYamlFile(
                        new File(LoginTo.getInstance().getDataFolder(), "messages.yml"),
                        "proxy-messages.yml",
                        MessagesKeys.MESSAGESVERSION
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
