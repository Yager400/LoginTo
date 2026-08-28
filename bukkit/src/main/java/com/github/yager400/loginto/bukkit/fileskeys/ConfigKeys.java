/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.fileskeys;

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
    SETTINGS_PREMIUM_ENABLED                        ("settings.premium.enabled"),
    SETTINGS_PREMIUM_CACHEDURATION                  ("settings.premium.cache-duration"),
    SETTINGS_PREMIUM_AUTOREGISTER                   ("settings.premium.auto-register"),
    SETTINGS_PREMIUM_PREMIUMBYPASSLIST              ("settings.premium.premium-bypass-list"),
    SETTINGS_SPAWNSETTING_ENABLED                   ("settings.spawn-setting.enabled"),
    SETTINGS_SPAWNSETTING_CORDS_X                   ("settings.spawn-setting.cords.x"),
    SETTINGS_SPAWNSETTING_CORDS_Y                   ("settings.spawn-setting.cords.y"),
    SETTINGS_SPAWNSETTING_CORDS_Z                   ("settings.spawn-setting.cords.z"),
    SETTINGS_SPAWNSETTING_CORDS_WORLD               ("settings.spawn-setting.cords.world"),
    SETTINGS_SPAWNSETTING_RESTOREPREVIOUSLOCATION   ("settings.spawn-setting.restore-previous-location"),
    SETTINGS_CHECKFORUPDATES                        ("settings.check-for-updates"),
    SETTINGS_SHOWWATERMARK                          ("settings.show-watermark"),
    SETTINGS_USEBUILTINPACKETEVENTS                 ("settings.use-built-in-packetevents");

    private final String s;

    ConfigKeys(String s) {
        this.s = s;
    }

    @Override
    public String value() {
        return s;
    }

}
