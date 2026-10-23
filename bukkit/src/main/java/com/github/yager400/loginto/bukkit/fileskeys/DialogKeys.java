/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.bukkit.fileskeys;

import com.github.yager400.loginto.common.data.files.ValueKey;

public enum DialogKeys implements ValueKey {

    DIALOGVERSION                                   ("dialog-version"),

    MESSAGES_BUTTONS_SENDPASSWORD                   ("messages.buttons.send-password"),
    MESSAGES_BUTTONS_DISCONNECT                     ("messages.buttons.disconnect"),

    MESSAGES_LABELS_PASSWORD                        ("messages.labels.password"),
    MESSAGES_LABELS_RETYPEPASSWORD                  ("messages.labels.retype-password"),

    MESSAGES_DIALOGTITLES_LOGIN                     ("messages.dialog-titles.login"),
    MESSAGES_DIALOGTITLES_REGISTER                  ("messages.dialog-titles.register"),
    MESSAGES_KICK_ERRORWHILEMANAGINGDIALOG          ("messages.kick.error-while-managing-dialog"),
    MESSAGES_KICK_LOGINKICK                         ("messages.kick.login-kick"),
    MESSAGES_KICK_REGISTERKICK                      ("messages.kick.register-kick");

    private final String s;

    DialogKeys(String s) {
        this.s = s;
    }

    @Override
    public String value() {
        return s;
    }
}
