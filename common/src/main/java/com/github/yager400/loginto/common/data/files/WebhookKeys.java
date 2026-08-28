/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.common.data.files;

public enum WebhookKeys implements ValueKey {

    DISCORDWEBHOOKURL       ("discord-webhook-url"),

    INTERACTIONS_REGISTER   ("interactions.register"),
    INTERACTIONS_LOGIN      ("interactions.login"),
    INTERACTIONS_UNREGISTER ("interactions.unregister");

    private final String s;

    WebhookKeys(String s) {
        this.s = s;
    }

    @Override
    public String value() {
        return s;
    }
}
