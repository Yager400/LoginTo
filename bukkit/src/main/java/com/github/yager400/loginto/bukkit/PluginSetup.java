/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.yager400.loginto.bukkit.commands.*;
import com.github.yager400.loginto.bukkit.events.*;
import com.github.yager400.loginto.bukkit.events.premium.AuthPacketEventListener;
import com.github.yager400.loginto.bukkit.fileskeys.ConfigKeys;
import com.github.yager400.loginto.bukkit.fileskeys.MessagesKeys;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.data.database.connectors.MySQLConnector;
import com.github.yager400.loginto.common.data.database.connectors.SQLiteConnector;
import com.github.yager400.loginto.common.data.dependencies.LibraryDownloader;
import com.github.yager400.loginto.common.data.dependencies.libbyextension.Library;
import com.github.yager400.loginto.common.data.files.FilesManager;
import com.github.yager400.loginto.common.data.files.YamlReader;
import com.github.yager400.loginto.common.utils.Updates;
import com.github.yager400.loginto.common.utils.WebHooks;
import com.github.yager400.loginto.folia.FoliaLib;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
        FilesManager.downloadRockYou(Paths.get(plugin.getDataFolder().getAbsolutePath(), FilesManager.getPluginDataFolderName(), "rockyou.txt"));

        Map<String, Path> files = new HashMap<>();
        files.put("bukkit-config.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "config.yml"));
        files.put("bukkit-messages.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "messages.yml"));
        files.put("webhooks.yml", Paths.get(plugin.getDataFolder().getAbsolutePath(), "webhooks.yml"));

        FilesManager.saveFiles(files, false);
    }

    public static void downloadDependencies(boolean installPacketEvent) {

        Plugin plugin = LoginTo.getInstance();

        plugin.getLogger().warning("If you get any exception due to a missing, broken or wrong library, delete the folder /lib into the LoginTo folder.");

        HashMap<String, String> relocations = new HashMap<>();
        relocations.put("com{}google{}zxing", "com{}github{}yager400{}loginto{}libs{}zxing");
        relocations.put("com{}warrenstrange{}googleauth", "com{}github{}yager400{}loginto{}libs{}googleauth");
        relocations.put("net{}kyori", "com{}github{}yager400{}loginto{}libs{}kyori");

        List<String> groupsIdToExclude = new ArrayList<>();
        groupsIdToExclude.add("io.netty");
        groupsIdToExclude.add("io.projectreactor");
        groupsIdToExclude.add("commons-logging");
        groupsIdToExclude.add("avalon-framework");
        groupsIdToExclude.add("org.checkerframework");

        List<Library> libraries = new ArrayList<>();
        libraries.add(new Library("com{}google{}zxing:core:3.5.3"));
        libraries.add(new Library("com{}warrenstrange:googleauth:1.5.0"));
        libraries.add(new Library("net{}kyori:adventure-text-serializer-legacy:4.26.1"));
        libraries.add(new Library("net{}kyori:adventure-text-minimessage:4.26.1"));
        libraries.add(new Library("net{}kyori:adventure-platform-api:4.3.4"));
        libraries.add(new Library("net{}kyori:adventure-platform-bukkit:4.3.4"));
        libraries.add(new Library("net{}kyori:adventure-api:4.26.1"));

        if (installPacketEvent) {
            libraries.add(new Library("com.github.retrooper:packetevents-api:2.13.0", Library.Resository.CODEMC));
            libraries.add(new Library("com.github.retrooper:packetevents-spigot:2.13.0", Library.Resository.CODEMC));
        }

        try {
            LibraryDownloader.downloadLibraries(
                    libraries,
                    relocations,
                    new BukkitLibraryManager(plugin),
                    Paths.get(plugin.getDataFolder().getAbsolutePath(), "lib"),
                    "4.0.0", // Change this only if a new library got added, updated or removed
                    groupsIdToExclude
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class PluginEventAndCommand {
        public static void initializeCommands() {
            JavaPlugin plugin = LoginTo.getJavaPlugin();
            plugin.getCommand("register").setExecutor(new RegisterCommand());
            plugin.getCommand("register").setExecutor(new RegisterCommand());
            plugin.getCommand("login").setExecutor(new LoginCommand());
            plugin.getCommand("login").setExecutor(new LoginCommand());
            plugin.getCommand("unregister").setExecutor(new UnRegisterCommand());
            plugin.getCommand("unregister").setExecutor(new UnRegisterCommand());
            plugin.getCommand("changepassword").setExecutor(new ChangePasswordCommand());
            plugin.getCommand("changepassword").setExecutor(new ChangePasswordCommand());
            plugin.getCommand("premium").setExecutor(new PremiumCommand());
            plugin.getCommand("premium").setExecutor(new PremiumCommand());
            plugin.getCommand("cracked").setExecutor(new CrackedCommand());
            plugin.getCommand("cracked").setExecutor(new CrackedCommand());
            plugin.getCommand("loginto").setExecutor(new LoginToCommand());
            plugin.getCommand("loginto").setExecutor(new LoginToCommand());
            plugin.getCommand("otp").setExecutor(new OTPCommand());
            plugin.getCommand("otp").setExecutor(new OTPCommand());

            PluginManager manager = Bukkit.getPluginManager();

            if (!LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED)) {
                Permission premiumMePerm =      manager.getPermission("loginto.premium.me");
                Permission premiumOtherPerm =   manager.getPermission("loginto.premium.other");
                Permission crackedMePerm =      manager.getPermission("loginto.cracked.me");
                Permission crackedOtherPerm =   manager.getPermission("loginto.cracked.other");
                if (premiumMePerm != null)      premiumMePerm.setDefault(PermissionDefault.FALSE);
                if (premiumOtherPerm != null)   premiumOtherPerm.setDefault(PermissionDefault.FALSE);
                if (crackedMePerm != null)      crackedMePerm.setDefault(PermissionDefault.FALSE);
                if (crackedOtherPerm != null)   crackedOtherPerm.setDefault(PermissionDefault.FALSE);
            }
        }

        public static void initializeListeners() {
            Plugin plugin = LoginTo.getInstance();
            PluginManager pluginManager = plugin.getServer().getPluginManager();
            pluginManager.registerEvents(new CancelledEvents(), plugin);
            pluginManager.registerEvents(new JoinEvent(), plugin);
            pluginManager.registerEvents(new LoggedFromAnotherLocationEvent(), plugin);
            pluginManager.registerEvents(new PreCommandProcessEvent(), plugin);
            pluginManager.registerEvents(new QuitEvent(), plugin);

            EventManager eventManager = PacketEvents.getAPI().getEventManager();
            eventManager.registerListener(new InventoryHiderPE(), PacketListenerPriority.LOWEST);
            if (LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PREMIUM_ENABLED) && !LoginTo.getConfigReader().getBoolean(ConfigKeys.SETTINGS_PROXY_BRIDGEBUKKITPROXY)) {
                if (!Bukkit.getOnlineMode()) {
                    eventManager.registerListener(new AuthPacketEventListener(), PacketListenerPriority.LOWEST);
                    if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
                        LoginTo.getInstance().getLogger().warning("No floodgate detected! If a bedrock player joins the server, they will be kicked.");
                    }
                } else {
                    plugin.getLogger().severe("The premium feature cannot be activated since the server is in online mode!\n" +
                            "To fix this, go to the server.properties file and change online-mode to false.");
                }
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
                FoliaLib.get().runTaskTimerAsync(() -> {
                    if (Updates.isThereAnUpdate(LoginTo.getInstance().getDescription().getVersion())) {
                        LoginTo.getInstance().getLogger().info("A new update for LoginTo is out! Check it on https://modrinth.com/plugin/login-to \n(This is a periodic message)");
                    }
                },  0, 72000L);
            }
        }

        public static void checkForFileUpdates() {
            try {
                FilesManager.updateYamlFile(
                        new File(LoginTo.getInstance().getDataFolder(), "config.yml"),
                        "bukkit-config.yml",
                        "1",
                        ConfigKeys.CONFIGVERSION
                );
                FilesManager.updateYamlFile(
                        new File(LoginTo.getInstance().getDataFolder(), "messages.yml"),
                        "bukkit-messages.yml",
                        "1",
                        MessagesKeys.MESSAGESVERSION
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
