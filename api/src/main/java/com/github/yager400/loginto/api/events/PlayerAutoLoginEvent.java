/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api.events;

import java.util.UUID;

/**
 * This event is called when a player successfully logs in into their account automatically via premium or bedrock account
 */
public class PlayerAutoLoginEvent {

    private final UUID playerUUID;
    private final AccountType accountType;

    public PlayerAutoLoginEvent(UUID playerUUID, AccountType accountType) {
        this.playerUUID = playerUUID;
        this.accountType = accountType;
    }

    /**
     * Get the player uuid
     * @return Player's uuid
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     * Get this player's account type upon auto-login
     * @return This player's account type
     */
    public AccountType getAccountType() {
        return this.accountType;
    }

    public enum AccountType {
        /**
         * This player has a valid Java account
         */
        PREMIUM,
        /**
         * This player has a valid Bedrock account
         */
        BEDROCK
    }

}
