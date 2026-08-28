plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "LoginTo"

include("common", "folia-lib", "bukkit", "bungeecord", "velocity")