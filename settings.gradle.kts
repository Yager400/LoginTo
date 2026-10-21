plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "LoginTo"

include("api", "common", "bukkit", "bungeecord", "velocity")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.alessiodp.com/releases")
        maven("https://repo.helpch.at/releases/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://jitpack.io")
        maven("https://repo.tcoded.com/releases")
    }
}
