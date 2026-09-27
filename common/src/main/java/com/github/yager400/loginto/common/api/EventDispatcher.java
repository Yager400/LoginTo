/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.api;

import com.github.yager400.loginto.api.events.*;

import java.util.UUID;

public class EventDispatcher {

    public static void callPlayerAccountDeletionEvent(UUID playerUUID, UUID adminUUID) {
        ApiUtils.callEvent(new PlayerAccountDeletionEvent(playerUUID, adminUUID));
    }

    public static void callPlayerAutoLoginEvent(UUID playerUUID, PlayerAutoLoginEvent.AccountType accountType) {
        ApiUtils.callEvent(new PlayerAutoLoginEvent(playerUUID, accountType));
    }

    public static void callPlayerChangePasswordEvent(UUID playerUUID, String newPassword) {
        ApiUtils.callEvent(new PlayerChangePasswordEvent(playerUUID, newPassword));
    }

    public static void callPlayerLoginEvent(UUID playerUUID, String password) {
        ApiUtils.callEvent(new PlayerLoginEvent(playerUUID, password));
    }

    public static void callPlayerRegistrationEvent(UUID playerUUID, String password) {
        ApiUtils.callEvent(new PlayerRegistrationEvent(playerUUID, password));
    }

}
