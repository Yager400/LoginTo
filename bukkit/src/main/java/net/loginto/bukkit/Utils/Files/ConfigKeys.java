/*
Copyright (C) 2026 Yager400

This file is part of this project, released under the terms of
the GNU General Public License v3.0.
See the LICENSE file for details.
 */
package net.loginto.bukkit.Utils.Files;

public enum ConfigKeys {
    CONFIG_VERSION("ConfigVersion"),

    COMMANDS_SETTINGS_PRE_LOGIN_ALLOWED_COMMANDS("commands-settings.pre-login-allowed-commands"),

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

    OTP_CONFIG_SERVER_NAME("otp-config.server-name"),

    SPAWN_SETTINGS_TELEPORT_ON_JOIN("spawn-settings.teleport-on-join"),
    SPAWN_SETTINGS_TARGET_DIMENSION("spawn-settings.target-dimension"),
    SPAWN_SETTINGS_SPAWN_COORDINATES_X("spawn-settings.spawn-coordinates.x"),
    SPAWN_SETTINGS_SPAWN_COORDINATES_Y("spawn-settings.spawn-coordinates.y"),
    SPAWN_SETTINGS_SPAWN_COORDINATES_Z("spawn-settings.spawn-coordinates.z"),
    SPAWN_SETTINGS_RESTORE_PREVIOUS_LOCATION("spawn-settings.restore-previous-location"),

    INTEGRATIONS_PROXY_SERVER_POST_LOGIN("integrations.proxy.server-post-login"),
    INTEGRATIONS_DISCORD_REGISTER_WEBHOOK_URL("integrations.discord.register-webhook-url"),
    INTEGRATIONS_DISCORD_REGISTER_MESSAGE("integrations.discord.register-message"),
    INTEGRATIONS_DISCORD_LOGIN_WEBHOOK_URL("integrations.discord.login-webhook-url"),
    INTEGRATIONS_DISCORD_LOGIN_MESSAGE("integrations.discord.login-message"),
    INTEGRATIONS_DISCORD_UNREGISTER_WEBHOOK_URL("integrations.discord.unregister-webhook-url"),
    INTEGRATIONS_DISCORD_UNREGISTER_MESSAGE("integrations.discord.unregister-message"),
    INTEGRATIONS_DISCORD_PASSWORD_CHANGE_WEBHOOK_URL("integrations.discord.password-change-webhook-url"),
    INTEGRATIONS_DISCORD_PASSWORD_CHANGE_MESSAGE("integrations.discord.password-change-message"),

    STORAGE_STORAGE_TYPE("storage.storage-type"),
    STORAGE_DATABASE_HOST("storage.database.host"),
    STORAGE_DATABASE_PORT("storage.database.port"),
    STORAGE_DATABASE_NAME("storage.database.name"),
    STORAGE_DATABASE_USER("storage.database.user"),
    STORAGE_DATABASE_PASSWORD("storage.database.password"),

    PREMIUM_ENABLE_PREMIUM_FEATURES("premium.enable-premium-features"),
    PREMIUM_STORAGE_DATABASE_TYPE("premium.storage.database-type"),
    PREMIUM_STORAGE_DATABASE_HOST("premium.storage.database.host"),
    PREMIUM_STORAGE_DATABASE_PORT("premium.storage.database.port"),
    PREMIUM_STORAGE_DATABASE_USER("premium.storage.database.user"),
    PREMIUM_STORAGE_DATABASE_PASSWORD("premium.storage.database.password"),
    PREMIUM_STORAGE_DATABASE_NAME("premium.storage.database.database-name"),

    LOGGING_LOGGING("logging.logging"),
    LOGGING_DATE_FORMAT("logging.date-format"),

    PLUGIN_UTILITY_ENABLE_UPDATE_CHECKER("plugin-utility.enable-update-checker"),
    PLUGIN_UTILITY_SHOW_WATERMARK("plugin-utility.show-watermark"),
    PLUGIN_UTILITY_USE_BUILT_IN_PACKETEVENTS_API("plugin-utility.use-built-in-packetevents-api"),
    PLUGIN_UTILITY_USE_EXPERIMENTAL_FEATURES("plugin-utility.enable-experimental-features");

    private final String s;

    ConfigKeys(String s) {
        this.s = s;
    }

    public String path() {
        return s;
    }
}
