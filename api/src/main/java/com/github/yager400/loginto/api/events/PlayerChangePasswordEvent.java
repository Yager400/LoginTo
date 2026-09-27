/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api.events;

import java.util.UUID;

/**
 * This event is called when a player successfully changes their password
 */
public class PlayerChangePasswordEvent {

    private final UUID playerUUID;
    private final String hashedPassword;

    public PlayerChangePasswordEvent(UUID playerUUID, String hashedPassword) {
        this.playerUUID = playerUUID;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Get the player uuid
     * @return Player's uuid
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * Get the password hash used on this event
     * @return The hash of the password (using BCrypt)
     */
    public String getHashedPassword() {
        return this.hashedPassword;
    }
}
