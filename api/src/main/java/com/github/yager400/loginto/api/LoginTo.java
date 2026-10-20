/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.api;

import com.github.yager400.loginto.api.utils.EventManager;

public class LoginTo {

    private static LoginToApi loginToApi;

    public static void setApi(LoginToApi loginToApi) {
        if (LoginTo.loginToApi == null) {
            LoginTo.loginToApi = loginToApi;
        }
    }

    /**
     * Get the LoginTo API containing usefull methods to use on your plugin
     * @return The LoginTo API instance
     * @since 4.2.0
     */
    public static LoginToApi getApi() {
        return LoginTo.loginToApi;
    }

    /**
     * @deprecated use {@link LoginToApi#getEventManager()}
     */
    @Deprecated
    public static EventManager getEventManager() {
        return LoginTo.getApi().getEventManager();
    }
}
