plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "LoginTo"

include("api", "common", "folia-lib", "bukkit", "bungeecord", "velocity")