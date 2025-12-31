/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */
package net.loginto.bungeecord;

import java.util.logging.Logger;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import net.loginto.bungeecord.Database.H2;
import net.loginto.bungeecord.Database.SQLite;
import net.loginto.bungeecord.Events.PluginMessage;
import net.loginto.bungeecord.Events.PreLogin;
import static net.loginto.bungeecord.Utility.FileMGR.createBungeeConfigFile;

public class LoginTo extends Plugin {

    private H2 h2;
    private SQLite sqlite;

    @Override
    public void onEnable() {

        Logger logger = getLogger();
        ProxyServer server = getProxy();

        logger.warning("Hello, thanks for using the LoginTo Premium feature, this version is still in BETA and i will really appreciate if you report any bug here, thank you\n https://github.com/Yager400/LoginTo/issues");

        createBungeeConfigFile();

        h2 = new H2(server, this);
        h2.connect();

        sqlite = new SQLite();
        sqlite.connect();

        server.getPluginManager().registerListener(this, new PreLogin(h2, sqlite, server, this));
        server.getPluginManager().registerListener(this, new PluginMessage(server, h2, logger));
    }

    @Override
    public void onDisable() {
        if (h2 != null) {
            h2.close();
        }
        if (sqlite != null) {
            sqlite.close();
        }
    }
}