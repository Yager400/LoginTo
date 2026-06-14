/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Utils.Files;

public enum MessageKeys {

    MESSAGE_VERSION("MessageVersion"),

    REGISTER_ERROR_ALREADY_REGISTERED("register.error.already-registered"),
    REGISTER_ERROR_CHARACTER_ERROR("register.error.register-character-error"),
    REGISTER_ERROR_PASSWORD_LENGTH("register.error.password-length"),
    REGISTER_ERROR_PASSWORD_MISMATCH("register.error.password-mismatch"),
    REGISTER_ERROR_USAGE("register.error.register-usage"),
    REGISTER_ERROR_PASSWORD_TOO_SIMPLE("register.error.password-too-simple"),
    REGISTER_PROMPT("register.register-prompt"),
    REGISTER_PROMPT_CHARACTERS("register.register-prompt-characters"),
    REGISTER_SUCCESS("register.register-success"),

    LOGIN_ERROR_NOT_REGISTERED("login.error.not-registered"),
    LOGIN_ERROR_ALREADY_LOGGED_IN("login.error.already-logged-in"),
    LOGIN_ERROR_USAGE("login.error.login-usage"),
    LOGIN_ERROR_WRONG_PASSWORD("login.error.wrong-password"),
    LOGIN_PROMPT("login.login-prompt"),
    LOGIN_SUCCESS("login.login-success"),
    LOGIN_PREMIUM_LOGIN_SUCCESS("login.login-premium-success"),
    LOGIN_BEDROCK_LOGIN_SUCCESS("login.login-bedrock-success"),

    UNREGISTER_ERROR_PLAYER_DOESNT_EXIST("unregister.error.player-doesnt-exist"),
    UNREGISTER_ERROR_NOT_CONFIRMED("unregister.error.unregister-not-confirmed"),
    UNREGISTER_ERROR_USAGE("unregister.error.unregister-usage"),
    UNREGISTER_ACCOUNT_UNREGISTERED("unregister.account-unregistered"),
    UNREGISTER_ADMIN_UNREGISTERED_ACCOUNT("unregister.admin-unregistered-account"),

    CRACKED_ERROR_ALREADY_CRACKED("cracked.error.already-cracked"),
    CRACKED_WARN("cracked.cracked-warn"),
    CRACKED_DONE("cracked.cracked-done"),

    PREMIUM_ERROR_ALREADY_PREMIUM("premium.error.already-premium"),
    PREMIUM_WARN("premium.premium-warn"),
    PREMIUM_DONE("premium.premium-done"),

    CHANGEPASSWORD_ERROR_USAGE("changepassword.error.changepassword-usage"),
    CHANGEPASSWORD_ERROR_OLD_PASSWORD_WRONG("changepassword.error.old-password-wrong"),
    CHANGEPASSWORD_ERROR_NOT_REGISTERED("changepassword.error.not-registered"),
    CHANGEPASSWORD_PASSWORD_CHANGED("changepassword.password-changed"),

    SERVERS_AUTH_SERVERS_ERROR("servers.auth-servers-error"),
    SERVERS_DESTINATIONS_SERVERS_ERROR("servers.destination-servers-error"),

    ERRORS_GENERAL_NO_PERMISSION("errors.general.no-permission"),
    ERRORS_ACTIVITY_BEFORE_LOGIN_ONCOMMAND("errors.activity-before-login.oncommand-when-not-authenticated"),
    ERRORS_LOGIN_FAIL_ONKICK_FAILED_LOGIN("errors.login-fail.onkick-for-failed-login"),
    ERRORS_LOGIN_FAIL_ONKICK_LONG_WAITING("errors.login-fail.onkick-for-long-waiting"),
    ERRORS_LOGIN_FAIL_ONKICK_SAME_NAME("errors.login-fail.onkick-for-joining-with-same-name"),
    ERRORS_LOGIN_FAIL_INVALID_USERNAME("errors.login-fail.invalid-username");

    private final String s;

    MessageKeys(String s) {
        this.s = s;
    }

    public String path() {
        return s;
    }
}