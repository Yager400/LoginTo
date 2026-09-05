/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity;

import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.database.connectors.MySQLConnector;
import com.github.yager400.loginto.common.data.database.connectors.SQLiteConnector;
import com.github.yager400.loginto.common.data.dependencies.LibraryDownloader;
import com.github.yager400.loginto.common.data.dependencies.libbyextension.Library;
import com.github.yager400.loginto.common.data.files.FilesManager;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.utils.Updates;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.velocity.commands.*;
import com.github.yager400.loginto.velocity.events.*;
import com.github.yager400.loginto.velocity.fileskeys.ConfigKeys;
import com.github.yager400.loginto.velocity.fileskeys.MessagesKeys;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import net.byteflux.libby.VelocityLibraryManager;

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
                    SQLiteConnector.getConfig(LoginTo.getDataDirectory().toFile()),
                    LoginTo.getDataDirectory(),
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

        Path dataDirectory = LoginTo.getDataDirectory();

        FilesManager.makePluginDataFolder(dataDirectory);
        FilesManager.downloadRockYou(Paths.get(dataDirectory.toFile().getAbsolutePath(), FilesManager.getPluginDataFolderName(), "rockyou.txt"));

        Map<String, Path> files = new HashMap<>();
        files.put("proxy-config.yml", Paths.get(dataDirectory.toFile().getAbsolutePath(), "config.yml"));
        files.put("proxy-messages.yml", Paths.get(dataDirectory.toFile().getAbsolutePath(), "messages.yml"));
        files.put("webhooks.yml", Paths.get(dataDirectory.toFile().getAbsolutePath(), "webhooks.yml"));

        FilesManager.saveFiles(files, false);
    }

    public static void downloadDependencies(VelocityLibraryManager manager) {

        LoginTo.getLogger().warn("If you get any exception due to a missing, broken or wrong library, delete the folder /lib into the LoginTo folder.");

        // Here in velocity we don't need any external api, so we run this only for downloading the necessary common api
        HashMap<String, String> relocations = new HashMap<>();
        List<String> groupsIdToExclude = new ArrayList<>();
        List<Library> libraries = new ArrayList<>();
        try {
            LibraryDownloader.downloadLibraries(
                    libraries,
                    relocations,
                    manager,
                    Paths.get(LoginTo.getDataDirectory().toFile().getAbsolutePath(), "lib"),
                    "4.0.0", // Change this only if a new library got added, updated or removed
                    groupsIdToExclude
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class PluginEventAndCommand {
        public static void initializeCommands() {
            CommandManager manager = LoginTo.getServer().getCommandManager();
            CommandMeta register = manager.metaBuilder("register").aliases("r").plugin(LoginTo.getInstance()).build();
            manager.register(register, new RegisterCommand());
            CommandMeta login = manager.metaBuilder("login").aliases("l").plugin(LoginTo.getInstance()).build();
            manager.register(login, new LoginCommand());
            CommandMeta unregister = manager.metaBuilder("unregister").aliases("delacc").plugin(LoginTo.getInstance()).build();
            manager.register(unregister, new UnregisterCommand());
            CommandMeta changepassword = manager.metaBuilder("changepassword").plugin(LoginTo.getInstance()).build();
            manager.register(changepassword, new ChangePasswordCommand());
            CommandMeta premium = manager.metaBuilder("premium").plugin(LoginTo.getInstance()).build();
            manager.register(premium, new PremiumCommand());
            CommandMeta cracked = manager.metaBuilder("cracked").plugin(LoginTo.getInstance()).build();
            manager.register(cracked, new CrackedCommand());
            CommandMeta loginto = manager.metaBuilder("loginto").plugin(LoginTo.getInstance()).build();
            manager.register(loginto, new LoginToCommand());
        }

        public static void initializeListeners() {
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new CancelledEvents());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new CommandEvent());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new DisconnectEvent());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new PermissionEvent());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new PLMessageEvent());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new PreLogin());
            LoginTo.getServer().getEventManager().register(LoginTo.getInstance(), new ServerChoseEvent());

            if (LoginTo.getServer().getPluginManager().getPlugin("floodgate").isEmpty()) {
                LoginTo.getLogger().warn("No floodgate detected! If a bedrock player joins the server, they will be kicked.");
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
                LoginTo.getServer().getScheduler().buildTask(LoginTo.getInstance(), () -> {
                    if (Updates.isThereAnUpdate(LoginTo.getContainer().getDescription().getVersion().orElse("0"))) {
                        LoginTo.getInstance().getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                    }
                }).delay(1, TimeUnit.HOURS);
            }
        }

        public static void checkForFileUpdates() {
            try {
                FilesManager.updateYamlFile(
                        new File(LoginTo.getDataDirectory().toFile(), "config.yml"),
                        "proxy-config.yml",
                        "1",
                        ConfigKeys.CONFIGVERSION
                );
                FilesManager.updateYamlFile(
                        new File(LoginTo.getDataDirectory().toFile(), "messages.yml"),
                        "proxy-messages.yml",
                        "1",
                        MessagesKeys.MESSAGESVERSION
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
