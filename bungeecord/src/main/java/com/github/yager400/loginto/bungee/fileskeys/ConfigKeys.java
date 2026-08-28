/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bungee.fileskeys;

import com.github.yager400.loginto.common.data.files.ValueKey;

public enum ConfigKeys implements ValueKey {

    CONFIGVERSION                                   ("config-version"),

    DATASTORE_DATABASETYPE                          ("datastore.database-type"),
    DATASTORE_HOST                                  ("datastore.host"),
    DATASTORE_PORT                                  ("datastore.port"),
    DATASTORE_DATABASE                              ("datastore.database"),
    DATASTORE_USER                                  ("datastore.user"),
    DATASTORE_PASSWORD                              ("datastore.password"),

    SETTINGS_PASSWORD_REQUIREDCHARACTERS            ("settings.password.required-character"),
    SETTINGS_PASSWORD_DECLINEONCOMMONPASSWORD       ("settings.password.decline-on-common-password"),
    SETTINGS_PASSWORD_PASSWORDLENGTH_MIN            ("settings.password.password-length.min"),
    SETTINGS_PASSWORD_PASSWIRDLENGTH_MAX            ("settings.password.password-length.max"),
    SETTINGS_PASSWORD_MAXLOGINATTEMPTS              ("settings.password.max-login-attempts"),
    SETTINGS_PASSWORD_AUTHENTICATIONTIMEOUT         ("settings.password.authentication-timeout"),
    SETTINGS_SESSIONS_ENABLED                       ("settings.sessions.enabled"),
    SETTINGS_SESSIONS_SESSIONDURATION               ("settings.sessions.session-duration"),
    SETTINGS_PROXY_BRIDGEBUKKITPROXY                ("settings.proxy.bridge-bukkit-proxy"),
    SETTINGS_PROXY_SERVERTELEPORTONPRELOGIN         ("settings.proxy.server-teleport-on-pre-login"),
    SETTINGS_PROXY_SERVERTELEPORTONPOSTLOGIN        ("settings.proxy.server-teleport-on-post-login"),
    SETTINGS_PREMIUM_ENABLED                        ("settings.premium.enabled"),
    SETTINGS_PREMIUM_CACHEDURATION                  ("settings.premium.cache-duration"),
    SETTINGS_PREMIUM_AUTOREGISTER                   ("settings.premium.auto-register"),
    SETTINGS_PREMIUM_PREMIUMBYPASSLIST              ("settings.premium.premium-bypass-list"),
    SETTINGS_CHECKFORUPDATES                        ("settings.check-for-updates"),
    SETTINGS_SHOWWATERMARK                          ("settings.show-watermark");

    private final String s;

    ConfigKeys(String s) {
        this.s = s;
    }

    @Override
    public String value() {
        return s;
    }

}
