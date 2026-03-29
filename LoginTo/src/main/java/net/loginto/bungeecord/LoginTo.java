/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bungeecord;

import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.loginto.bungeecord.Database.Database;
import net.loginto.bungeecord.Database.SQLite;
import net.loginto.bungeecord.Events.*;
import net.loginto.bungeecord.Utility.LibraryDownloader;
import net.loginto.bungeecord.Utility.Metrics;

import static net.loginto.bungeecord.Utility.FileMGR.createBungeeConfigFile;

public class LoginTo extends Plugin {

    private Database database;
    private SQLite sqlite;

    @Override
    public void onEnable() {

        @SuppressWarnings("unused")
        Metrics metric = new Metrics(this, 30332);

        Logger logger = getLogger();
        ProxyServer server = getProxy();

        LibraryDownloader.Libs(this);

        createBungeeConfigFile(this);

        database = new Database(server, this, logger);
        database.connect();

        sqlite = new SQLite();
        sqlite.connect();

        server.getPluginManager().registerListener(this, new PreLogin(database, sqlite, server, this));
        server.getPluginManager().registerListener(this, new CommandEvent(database));
        server.getPluginManager().registerListener(this, new DisconnectEvent(database));
        server.getPluginManager().registerListener(this, new onPluginMessage(database));
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.close();
        }
        if (sqlite != null) {
            sqlite.close();
        }
    }
}