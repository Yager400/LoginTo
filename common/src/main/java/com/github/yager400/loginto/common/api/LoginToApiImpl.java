/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.api;

import com.github.yager400.loginto.api.LoginToApi;
import com.github.yager400.loginto.api.utils.EventManager;
import com.github.yager400.loginto.common.data.database.Database;
import com.github.yager400.loginto.common.players.Sessions;

import java.util.UUID;

public class LoginToApiImpl implements LoginToApi {

    private final Database database;
    private final EventManager eventManager;

    public LoginToApiImpl(Database database, EventManager eventManager) {
        this.database = database;
        this.eventManager = eventManager;
    }

    @Override
    public boolean isPlayerLogged(UUID uuid) {
        return Sessions.isPlayerLogged(uuid);
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        return this.database.databaseContainsPlayer(uuid);
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }
}
