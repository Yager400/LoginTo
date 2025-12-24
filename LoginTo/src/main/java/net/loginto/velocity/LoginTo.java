/*
Copyright (C) 2025 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0 or (at your option) any later version.
See the LICENSE file for details.
 */

package net.loginto.velocity;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;

import net.loginto.velocity.Database.H2;
import net.loginto.velocity.Database.SQLite;
import net.loginto.velocity.Events.PluginMessage;
import net.loginto.velocity.Events.PreLogin;

import static net.loginto.velocity.Utility.FileMGR.createVeloConfigFile;


public class LoginTo {

    private final ProxyServer server;
    private final Logger logger;
    private H2 h2;
    private SQLite sqlite;

    @Inject
    public LoginTo(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {

        logger.warn("Hello, thanks for using the LoginTo Premium feature, this version is still in BETA and i will really appreciate if you report any bug here, thank you\n https://github.com/Yager400/LoginTo/issues");

        createVeloConfigFile();

        h2 = new H2(server, this);
        h2.connect();

        sqlite = new SQLite();
        sqlite.connect();

        server.getEventManager().register(this, new PreLogin(server, h2, sqlite));
        server.getEventManager().register(this, new PluginMessage(server, h2, logger));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (h2 != null) {
            h2.close();
        }
        if (sqlite != null) {
            sqlite.close();
        }
    }
}

