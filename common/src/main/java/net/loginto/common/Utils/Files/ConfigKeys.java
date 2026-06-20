/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.common.Utils.Files;

public enum ConfigKeys {
    CONFIG_VERSION("ConfigVersion"),

    COMMANDS_SETTINGS_PRE_LOGIN_ALLOWED_COMMANDS("commands-settings.pre-login-allowed-commands"),

    SERVERS_AUTH_SERVERS("servers.auth-servers"),
    SERVER_DESTINATION_SERVERS("servers.destination-servers"),

    AUTH_SECURITY_KICK_ON_INVALID_PASSWORD("auth-security.kick-on-invalid-password"),
    AUTH_SECURITY_MAX_LOGIN_ATTEMPTS("auth-security.max-login-attempts"),
    AUTH_SECURITY_KICK_ON_AUTH_TIMEOUT("auth-security.kick-on-auth-timeout"),
    AUTH_SECURITY_AUTH_TIMEOUT_SECONDS("auth-security.auth-timeout-seconds"),

    PASSWORD_REQUIREMENTS_REQUIRE_SPECIAL_CHARS("password-requirements.require-special-chars"),
    PASSWORD_REQUIREMENTS_REQUIRED_CHAR_LIST("password-requirements.required-char-list"),
    PASSWORD_REQUIREMENTS_LENGTH_CHECK_ENABLED("password-requirements.length-check.enabled"),
    PASSWORD_REQUIREMENTS_LENGTH_CHECK_MIN_LENGTH("password-requirements.length-check.min-length"),
    PASSWORD_REQUIREMENTS_LENGTH_CHECK_MAX_LENGTH("password-requirements.length-check.max-length"),
    PASSWORD_REQUIREMENTS_BANNED_PASSWORD_DECLINE_ON_COMMON("password-requirements.banned-password.decline-on-common-password"),
    PASSWORD_REQUIREMENTS_BANNED_PASSWORD_USE_ROCKYOU("password-requirements.banned-password.use-rockyou"),
    PASSWORD_REQUIREMENTS_BANNED_PASSWORD_LIST("password-requirements.banned-password.banned-password"),

    INTEGRATIONS_DISCORD_REGISTER_WEBHOOK_URL("integrations.discord.register-webhook-url"),
    INTEGRATIONS_DISCORD_REGISTER_MESSAGE("integrations.discord.register-message"),
    INTEGRATIONS_DISCORD_LOGIN_WEBHOOK_URL("integrations.discord.login-webhook-url"),
    INTEGRATIONS_DISCORD_LOGIN_MESSAGE("integrations.discord.login-message"),
    INTEGRATIONS_DISCORD_UNREGISTER_WEBHOOK_URL("integrations.discord.unregister-webhook-url"),
    INTEGRATIONS_DISCORD_UNREGISTER_MESSAGE("integrations.discord.unregister-message"),

    STORAGE_STORAGE_TYPE("storage.storage-type"),
    STORAGE_DATABASE_HOST("storage.database.host"),
    STORAGE_DATABASE_PORT("storage.database.port"),
    STORAGE_DATABASE_NAME("storage.database.name"),
    STORAGE_DATABASE_USER("storage.database.user"),
    STORAGE_DATABASE_PASSWORD("storage.database.password"),

    PREMIUM_PREMIUM_FEATURES("premium.premium-features"),
    PREMIUM_AUTO_REGISTER("premium.auto-register"),

    PLUGIN_UTILITY_ENABLE_UPDATE_CHECKER("plugin-utility.enable-update-checker"),
    PLUGIN_UTILITY_SHOW_WATERMARK("plugin-utility.show-watermark");

    private final String s;

    ConfigKeys(String s) {
        this.s = s;
    }

    public String path() {
        return s;
    }
}
