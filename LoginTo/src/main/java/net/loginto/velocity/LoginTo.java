/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */

package net.loginto.velocity;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import net.loginto.velocity.Database.Database;
import net.loginto.velocity.Database.SQLite;
import net.loginto.velocity.Events.*;
import net.loginto.velocity.Utility.LibraryDownloader;

import static net.loginto.velocity.Utility.FileMGR.createVeloConfigFile;

import java.nio.file.Path;


public class LoginTo {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private Database database;
    private SQLite sqlite;

    @Inject
    public LoginTo(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {

        logger.warn("Hello, thanks for using the LoginTo Premium feature, this version is still in BETA and i will really appreciate if you report any bug here, thank you\n https://github.com/Yager400/LoginTo/issues");

        LibraryDownloader.Libs(this, logger, dataDirectory, server);

        createVeloConfigFile(this);

        database = new Database(server, this, logger);
        database.connect();

        sqlite = new SQLite();
        sqlite.connect();

        server.getEventManager().register(this, new PreLogin(server, database, sqlite, this));
        server.getEventManager().register(this, new PluginMessage(server, database, logger));
        server.getEventManager().register(this, new CommandEvent(database));
        server.getEventManager().register(this, new DisconnectEv(database));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (database != null) {
            database.close();
        }
        if (sqlite != null) {
            sqlite.close();
        }
    }
}

