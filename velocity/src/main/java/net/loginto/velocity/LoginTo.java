/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import net.byteflux.libby.VelocityLibraryManager;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.Database.Implementation.MySQL;
import net.loginto.common.Database.Implementation.SQLite;
import net.loginto.velocity.Commands.*;
import net.loginto.velocity.Events.*;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.velocity.Utils.Files.LoginToFiles;
import net.loginto.velocity.Utils.Updates;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.loginto.common.Utils.LibraryDownloader;

import java.nio.file.Path;


public class LoginTo {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private Database database;
    private PremiumSQLiteCache sqliteCache;
    private Metrics.Factory makeFactory;

    @Inject
    public LoginTo(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory makeFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.makeFactory = makeFactory;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {

        LibraryDownloader.Libs(new VelocityLibraryManager<>(
                logger,
                dataDirectory,
                server.getPluginManager(),
                this
        ));

        Metrics metrics = makeFactory.make(this, 31987);

        if (!server.getPluginManager().isLoaded("luckperms")) {
            logger.warn("LuckPerms not detected!, make sure to install it for configuring permissions. (default permission will be used, but you won't have the /unregister command without perms)");
        } else {
            logger.warn("LuckPerms detected!, see the permissions there -> https://modrinth.com/plugin/login-to");
        }

        LoginToFiles.saveFiles(this);
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path())) {
            LoginToFiles.downloadRockYou(logger);
        }

        String host = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_NAME.path());
        int port = LoginToFiles.Config.getInt(ConfigKeys.STORAGE_DATABASE_PORT.path());
        String username = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_USER.path());
        String password = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_PASSWORD.path());
        String dbName = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_NAME.path());

        if (LoginToFiles.Config.getString(ConfigKeys.STORAGE_STORAGE_TYPE.path()).equalsIgnoreCase("mysql")) {
            database = new MySQL(host, port, username, password, dbName);
        } else {
            database = new SQLite(dbName);
        }
        database.connect(host, port, username, password, dbName);

        sqliteCache = new PremiumSQLiteCache();
        sqliteCache.connect();

        server.getEventManager().register(this, new PreLogin(database, server, logger, sqliteCache));
        server.getEventManager().register(this, new CommandEvent());
        server.getEventManager().register(this, new ServerChoseEvent(database, server, this));
        server.getEventManager().register(this, new PluginMessage());
        server.getEventManager().register(this, new Disconnect());
        server.getEventManager().register(this, new PermissionSetup());

        CommandManager commandManager = server.getCommandManager();

        CommandMeta loginMeta = commandManager.metaBuilder("login")
                .aliases("l")
                .plugin(this)
                .build();
        commandManager.register(loginMeta, new Login(database, server, logger));

        CommandMeta registerMeta = commandManager.metaBuilder("register")
                .aliases("r")
                .plugin(this)
                .build();
        commandManager.register(registerMeta, new Register(database, logger, server, this, sqliteCache));

        CommandMeta unregister = commandManager.metaBuilder("unregister")
                .plugin(this)
                .build();
        commandManager.register(unregister, new UnRegister(database, server, logger, sqliteCache));

        CommandMeta premium = commandManager.metaBuilder("premium")
                .plugin(this)
                .build();
        commandManager.register(premium, new Premium(database));

        CommandMeta cracked = commandManager.metaBuilder("cracked")
                .plugin(this)
                .build();
        commandManager.register(cracked, new Cracked(database));

        CommandMeta changepassword = commandManager.metaBuilder("changepassword")
                .plugin(this)
                .build();
        commandManager.register(changepassword, new ChangePassword(this, database, server, logger));

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_ENABLE_UPDATE_CHECKER.path())) {
            Updates.checkForUpdates(server, logger, this);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (database != null) {
            database.close();
        }
        if (sqliteCache != null) {
            sqliteCache.close();
        }
    }
}

