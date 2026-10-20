/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api;

import com.github.yager400.loginto.api.utils.EventManager;

import java.util.UUID;

public interface LoginToApi {

    /**
     * Check if a player is logged or not
     * @param uuid The player's uuid
     * @return True if it's logged, otherwise false
     */
    boolean isPlayerLogged(UUID uuid);

    /**
     * Check if a player has already an account on the server
     * @param uuid The player's uuid
     * @return True if the account exists, otherwise false
     */
    boolean isPlayerRegistered(UUID uuid);

    /**
     * Get the EventManager for registering events
     * @return The EventManager instance
     */
    EventManager getEventManager();
}
