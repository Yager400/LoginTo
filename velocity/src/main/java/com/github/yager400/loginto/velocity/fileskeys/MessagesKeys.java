/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package com.github.yager400.loginto.velocity.fileskeys;

import com.github.yager400.loginto.common.data.files.ValueKey;

public enum MessagesKeys implements ValueKey {

    MESSAGESVERSION                             ("messages-version"),

    REGISTER_ALREADYREGISTERED                  ("register.already-registered"),
    REGISTER_NOREQUIREDCHARACTERERROR           ("register.no-required-character-error"),
    REGISTER_PASSWORDLENGTHERROR                ("register.password-length-error"),
    REGISTER_PASSWORDCONFIRMATIONMISMATCH       ("register.password-confirmation-mismatch"),
    REGISTER_PASSWORDISTOOSIMPLE                ("register.password-is-too-simple"),
    REGISTER_REGISTERPROMPT                     ("register.register-prompt"),
    REGISTER_REGISTERPROMPTREQUIRECHARACTERS    ("register.register-prompt-require-characters"),
    REGISTER_REGISTRATIONSUCCESS                ("register.registration-success"),

    LOGIN_NOTREGISTERED                         ("login.not-registered"),
    LOGIN_ALREADYLOGGEDIN                       ("login.already-logged-in"),
    LOGIN_WRONGPASSWORD                         ("login.wrong-password"),
    LOGIN_LOGINPROMPT                           ("login.login-prompt"),
    LOGIN_LOGINSUCCESS                          ("login.login-success"),

    UNREGISTER_INVALIDPLAYER                    ("unregister.invalid-player"),
    UNREGISTER_CANNOTUNREGISTERYOURSELF         ("unregister.cannot-unregister-yourself"),
    UNREGISTER_UNREGISTERSUCCESS                ("unregister.unregister-success"),
    UNREGISTER_ADMINUNREGISTERSUCCESS           ("unregister.admin-unregister-success"),

    CHANGEPASSWORD_WRONGOLDPASSWORD             ("changepassword.wrong-old-password"),
    CHANGEPASSWORD_NOTREGISTERED                ("changepassword.not-registered"),
    CHANGEPASSWORD_NOREQUIREDCHARACTERSERROR    ("changepassword.no-required-character-error"),
    CHANGEPASSWORD_PASSWORDLENGTHERROR          ("changepassword.password-length-error"),
    CHANGEPASSWORD_PASSWORDISTOOSIMPLE          ("changepassword.password-is-too-simple"),
    CHANGEPASSWORD_PASSWORDCHANGED              ("changepassword.password-changed"),

    PREMIUM_CRACKED_ALREADYCRACKED              ("premium.cracked.already-cracked"),
    PREMIUM_CRACKED_CRACKEDWARN                 ("premium.cracked.cracked-warn"),
    PREMIUM_CRACKED_CRACKEDSUCCESS              ("premium.cracked.cracked-success"),
    PREMIUM_PREMIUM_ALREADYPREMIUM              ("premium.premium.already-premium"),
    PREMIUM_PREMIUM_PREMIUMWARN                 ("premium.premium.premium-warn"),
    PREMIUM_PREMIUM_PREMIUMSUCCESS              ("premium.premium.premium-success"),
    PREMIUM_REGISTRATION_AUTOREGISTERPREMIUM    ("premium.registration.auto-register-premium"),
    PREMIUM_REGISTRATION_AUTOREGISTERBEDROCK    ("premium.registration.auto-register-bedrock"),
    PREMIUM_LOGIN_AUTOLOGINPREMIUM              ("premium.login.auto-login-premium"),
    PREMIUM_LOGIN_AUTOLOGINBEDROCK              ("premium.login.auto-login-bedrock"),
    PREMIUM_SKIPPEDAUTHENTICATION               ("premium.skipped-authentication"),
    PREMIUM_JAVAPLAYERMARKEDASBEDROCK           ("premium.java-player-marked-as-bedrock"),
    PREMIUM_LOGGEDINWITHDIFFERENTUUID           ("premium.logged-in-with-different-uuid"),

    SESSIONS_LOGGEDINWITHSESSION                ("sessions.logged-in-with-session"),

    DURINGLOGIN_EXECUTECOMMANDPRELOGIN          ("during-login.execute-command-pre-login"),
    DURINGLOGIN_KICKEDFORLONGWAITING            ("during-login.kicked-for-long-waiting"),
    DURINGLOGIN_KICKEDFORFAILINGTHELOGIN        ("during-login.kicked-for-failing-the-login"),
    DURINGLOGIN_ANOTHERPLAYERWITHSAMENAME       ("during-login.another-player-with-same-name");

    private final String s;

    MessagesKeys(String s) {
        this.s = s;
    }

    @Override
    public String value() {
        return s;
    }
}
