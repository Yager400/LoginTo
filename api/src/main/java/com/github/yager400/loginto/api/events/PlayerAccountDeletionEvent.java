/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api.events;

import java.util.UUID;

/**
 * This event is called when an admin has successfully deleted a user account<br>
 * (With the command /unregister user)
 */
public class PlayerAccountDeletionEvent {

    private final UUID playerUUID;
    private final UUID adminUUID;

    public PlayerAccountDeletionEvent(UUID playerUUID, UUID adminUUID) {
        this.playerUUID = playerUUID;
        this.adminUUID = adminUUID;
    }

    /**
     * Get the player's uuid
     * @return Player's uuid
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * Get the admin's uuid (The player that deleted the account)
     * If the console deleted the account, the uuid will be 0
     * @return Admin's uuid
     */
    public UUID getAdminUUID() {
        return this.adminUUID;
    }
}
