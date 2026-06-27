/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.loginto.bukkit.Commands.*;
import net.loginto.bukkit.Database.DatabaseConnectionUtils;
import net.loginto.bukkit.Events.Listeners;
import net.loginto.bukkit.PlayerUtils.PeriodicMessages;
import net.loginto.bukkit.Database.Database;
import net.loginto.bukkit.Utils.Files.ConfigKeys;
import net.loginto.bukkit.Utils.Files.LoginToFiles;
import net.loginto.bukkit.Utils.Files.MessageKeys;
import net.loginto.bukkit.Utils.Dependencies.Libraries;
import net.loginto.bukkit.Utils.Premium.PremiumCache;
import net.loginto.bukkit.Utils.TemporaryPremiumFeatureConfig;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import net.loginto.bukkit.Utils.YMLVersion;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LoginTo extends JavaPlugin {

    private Database database;
    private static net.kyori.adventure.platform.bukkit.BukkitAudiences adventure;

    @Override
    public void onLoad() {
        //Save files
        LoginToFiles.saveFiles(this);
        //-----

        getLogger().warning("If you get class exceptions, go into the plugin's folder and delete the 'lib' folder");

        if (getServer().getPluginManager().getPlugin("packetevents") != null && LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_USE_BUILT_IN_PACKETEVENTS_API.path(), this)) {
            LoginToFiles.Config.setConfigValue(ConfigKeys.PLUGIN_UTILITY_USE_BUILT_IN_PACKETEVENTS_API.path(), false, this);
            getLogger().info("PacketEvents already detected in the server, the built-in api will not be loaded");
        } else if (getServer().getPluginManager().getPlugin("packetevents") == null && !LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_USE_BUILT_IN_PACKETEVENTS_API.path(), this)) {
            LoginToFiles.Config.setConfigValue(ConfigKeys.PLUGIN_UTILITY_USE_BUILT_IN_PACKETEVENTS_API.path(), true, this);
            getLogger().severe("PacketEvents plugin not detected, the api will be automatically downloaded by the plugin");
        }

        Libraries.loadLibs(this);

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        getLogger().warning("LoginTo started loading...");

        TemporaryPremiumFeatureConfig.setPremiumFeature(this, "1.12");

        adventure = net.kyori.adventure.platform.bukkit.BukkitAudiences.create(this);

        if (!new File(getDataFolder(), "rockyou.txt").exists()) {
            getLogger().info("Downloading rockyou.txt");
            LoginToFiles.downloadRockYou(this);
        }

        if (Bukkit.getOnlineMode()) {
            getLogger().warning("Your server is in online mode, LoginTo will still work, but every player will be 100% premium (so they are the real owners of that account).\nLoginTo will still ask for the password during login");
        }

        if (!PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16)) {
            getLogger().warning("Legacy color and legacy minimessage will be used since the server version is under 1.16");
        }

        PacketEvents.getAPI().init();

        //Metrics
        Metrics metrics = new Metrics(this, 28083);
        //----

        //Updating yaml files
        //TODO
        try {
            YMLVersion.builder()
                    .plugin(this)
                    .version("1.12")
                    .resource("config.yml")
                    .versionKey(ConfigKeys.CONFIG_VERSION.path())
                    .build();
            YMLVersion.builder()
                    .plugin(this)
                    .version("1.8")
                    .resource("messages.yml")
                    .versionKey(MessageKeys.MESSAGE_VERSION.path())
                    .build();
            if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_USE_EXPERIMENTAL_FEATURES.path(), this)) {
                YMLVersion.builder()
                        .plugin(this)
                        .version("1.1")
                        .resource("experimental.yml")
                        .versionKey("ExperimentalVersion")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-----

        //Initializing databases
        String databaseType = LoginToFiles.Config.getString(ConfigKeys.STORAGE_STORAGE_TYPE.path(), this);
        database = null;
        switch (databaseType) {
            case "sqlite":      database = DatabaseConnectionUtils.connectSQLite(this); break;
            case "mysql":       database = DatabaseConnectionUtils.connectMySQL(this); break;
            case "postgresql":  database = DatabaseConnectionUtils.connectPostgreSQL(this); break;
            case "h2":          database = DatabaseConnectionUtils.connectH2(this); break;
            default:
                getLogger().severe("Database type '" + databaseType + "' is not valid, using sqlite");
                database = DatabaseConnectionUtils.connectSQLite(this);
                databaseType = "sqlite";
                break;
        }
        final String finalDatabaseType = databaseType;
        metrics.addCustomChart(new SimplePie("storage_type_used", () -> finalDatabaseType));

        //Registering listener
        Listeners.registerAllListener(this, database);
        //-----

        //Add commands
        getCommand("register").setExecutor(new Register(this, database));
        getCommand("register").setTabCompleter(new Register(this, database));
        getCommand("register").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.register"));

        getCommand("login").setExecutor(new Login(this, database));
        getCommand("login").setTabCompleter(new Login(this, database));
        getCommand("login").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.login"));

        getCommand("unregister").setExecutor(new UnRegister(this, database));
        getCommand("unregister").setTabCompleter(new UnRegister(this, database));
        getCommand("unregister").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.unregister"));

        getCommand("changepassword").setExecutor(new ChangePassword(this, database));
        getCommand("changepassword").setTabCompleter(new ChangePassword(this, database));
        getCommand("changepassword").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.changepassword"));

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PREMIUM_ENABLE_PREMIUM_FEATURES.path(), this)) {
            getCommand("premium").setExecutor(new Premium(this, database));
            getCommand("premium").setTabCompleter(new Premium(this, database));
            getCommand("premium").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.premium"));

            getCommand("cracked").setExecutor(new Cracked(this, database));
            getCommand("cracked").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.cracked"));
        } else {
            Permission premiumMePerm = Bukkit.getPluginManager().getPermission("loginto.premium.me");
            Permission permissionOtherPerm = Bukkit.getPluginManager().getPermission("loginto.premium.other");
            Permission crackedPerm = Bukkit.getPluginManager().getPermission("loginto.cracked.me");
            if (premiumMePerm != null) premiumMePerm.setDefault(PermissionDefault.FALSE);
            if (permissionOtherPerm != null) permissionOtherPerm.setDefault(PermissionDefault.FALSE);
            if (crackedPerm != null) crackedPerm.setDefault(PermissionDefault.FALSE);
        }

        getCommand("getlogs").setExecutor(new getlogs(this));
        getCommand("getlogs").setTabCompleter(new getlogs(this));
        getCommand("getlogs").getAliases().addAll(getConfig().getStringList("commands-settings.command-aliases.getlogs"));

        getCommand("otp").setExecutor(new OTP(this, database));
        //-----

        //Register channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //------

        //Periodic messages
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_ENABLE_UPDATE_CHECKER.path(), this)) {
            PeriodicMessages.checkForUpdates(this);
        }
        PeriodicMessages.otpPeriodicMessage(this);
        //-----

        getLogger().warning("LoginTo loaded!");

    }

    public static net.kyori.adventure.platform.bukkit.BukkitAudiences getAdventure() {
        return adventure;
    }

    @Override
    public void onDisable() {
        database.close();
        PacketEvents.getAPI().terminate();
        PremiumCache.closeIfOpen();
        adventure.close();
    }
}
