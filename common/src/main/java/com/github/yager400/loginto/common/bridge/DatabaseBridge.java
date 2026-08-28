/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.bridge;

import com.github.yager400.loginto.common.data.database.Database;
import com.sun.jdi.InvalidTypeException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Bridge for connecting a backend server to the proxy
 */
public class DatabaseBridge {

    private final Database database;

    public DatabaseBridge(Database database, String databaseType) throws InvalidTypeException {
        if (!databaseType.equalsIgnoreCase("mysql")) {
            throw new InvalidTypeException("The database type must be MYSQL for using the bridge");
        }

        this.database = database;

        initDatabase();
    }

    private void initDatabase() {
        database.executeQuery("CREATE TABLE IF NOT EXISTS LoginTo_Auth_Bridge(uuid VARCHAR(36) NOT NULL UNIQUE);");

        // Clean up the old logged player
        database.executeQuery("DELETE FROM LoginTo_Auth_Bridge;");
    }

    public void addPlayer(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            database.executeQuery("INSERT INTO LoginTo_Auth_Bridge(uuid) VALUES (?);", uuid.toString());
        });
    }

    public void removePlayer(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            database.executeQuery("DELETE FROM LoginTo_Auth_Bridge WHERE uuid = ?;", uuid.toString());
        });
    }

    public boolean isPlayerLogged(UUID uuid) {
        try {
            return database.executeQuerySet("SELECT 1 FROM LoginTo_Auth_Bridge WHERE uuid = ?;", uuid.toString()).next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
