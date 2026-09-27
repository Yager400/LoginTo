/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api;

import com.github.yager400.loginto.api.utils.EventManager;

public class LoginTo {

    private static EventManager eventManager;

    public static void setEventManager(EventManager eventManager) {
        if (LoginTo.eventManager == null) {
            LoginTo.eventManager = eventManager;
        }
    }

    /**
     * Get the EventManager for registering events
     * @return The EventManager instance
     */
    public static EventManager getEventManager() {
        return LoginTo.eventManager;
    }
}
