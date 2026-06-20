/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord;

import net.byteflux.libby.BungeeLibraryManager;
import net.byteflux.libby.LibraryManager;
import net.loginto.bungeecord.Commands.*;
import net.loginto.bungeecord.Events.*;
import net.loginto.bungeecord.Utils.Files.LoginToFiles;
import net.loginto.bungeecord.Utils.Updates;
import net.loginto.common.Database.Cache.PremiumSQLiteCache;
import net.loginto.common.Database.Database;
import net.loginto.common.Database.Implementation.MySQL;
import net.loginto.common.Database.Implementation.SQLite;
import net.loginto.common.Utils.Files.ConfigKeys;
import net.loginto.common.Utils.LibraryDownloader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.util.logging.Logger;

public class LoginTo extends Plugin  {

    private Database database;
    private PremiumSQLiteCache sqliteCache;

    @Override
    public void onEnable() {

        ProxyServer server = getProxy();
        Logger logger = getLogger();

        LibraryManager libManager = new BungeeLibraryManager(this);
        LibraryDownloader.Libs(libManager);
        LibraryDownloader.downloadKyoriDependency(libManager);

        Metrics metrics = new Metrics(this, 31988);

        if (getProxy().getPluginManager().getPlugin("LuckPerms") == null) {
            logger.warning("LuckPerms not detected!, use the config.yml file to configure permission, see them there -> https://modrinth.com/plugin/login-to");
        } else {
            logger.warning("LuckPerms detected!, see the permissions there -> https://modrinth.com/plugin/login-to");
        }

        LoginToFiles.saveFiles(this);
        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU.path())) {
            LoginToFiles.downloadRockYou(logger);
        }

        String host = LoginToFiles.Config.getString(ConfigKeys.STORAGE_DATABASE_HOST.path());
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

        getProxy().getPluginManager().registerListener(this, new PreLogin(database, server, logger, sqliteCache));
        getProxy().getPluginManager().registerListener(this, new CommandEvent());
        getProxy().getPluginManager().registerListener(this, new ServerChoseEvent(database, server, this));
        getProxy().getPluginManager().registerListener(this, new PluginMessage());
        getProxy().getPluginManager().registerListener(this, new Disconnect());

        getProxy().getPluginManager().registerCommand(this, new Login(database, server, logger));
        getProxy().getPluginManager().registerCommand(this, new Register(database, logger, server, this, sqliteCache));
        getProxy().getPluginManager().registerCommand(this, new UnRegister(database, server, logger, sqliteCache));
        getProxy().getPluginManager().registerCommand(this, new Premium(database));
        getProxy().getPluginManager().registerCommand(this, new Cracked(database));
        getProxy().getPluginManager().registerCommand(this, new ChangePassword(this, database, server, logger));

        if (LoginToFiles.Config.isFeatureEnabled(ConfigKeys.PLUGIN_UTILITY_ENABLE_UPDATE_CHECKER.path())) {
            Updates.checkForUpdates(server, logger, this);
        }
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
        if (sqliteCache != null) {
            sqliteCache.close();
        }
    }
}

