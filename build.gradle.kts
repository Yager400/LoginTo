import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

val minecraftTestServerVersion = "26.1.2"
val minecraftTestServerJversion = 25

val javaVersion = JavaVersion.VERSION_17
val spigotVersion = "1.13-R0.1-SNAPSHOT"
val velocityVersion = "3.4.0-SNAPSHOT"
val bungeeVersion = "1.20-R0.1"
val hikariVersion = "4.0.3"
val packetEventsVersion = "2.13.0"
val placeholderApiVersion = "2.12.2"
val sqliteVersion = "3.51.0.0"
val mysqlVersion = "8.2.0"
val postgresVersion = "42.7.8"
val h2Version = "2.4.240"
val libbyVersion = "1.3.1"
val bcryptVersion = "0.4"
val adventureVersion = "4.26.1"
val minimessageVersion = "4.26.1"
val bukkitPlatformAdventure = "4.3.4"
val zxingVersion = "3.5.3"
val googleauthVersion = "1.5.0"
val floodgateVersion = "2.2.0-SNAPSHOT"
val bstatsVersion = "3.1.0"
val mavenModelVersion = "3.9.9"

extra["spigotVersion"] = spigotVersion
extra["velocityVersion"] = velocityVersion
extra["bungeeVersion"] = bungeeVersion
extra["hikariVersion"] = hikariVersion
extra["packetEventsVersion"] = packetEventsVersion
extra["placeholderApiVersion"] = placeholderApiVersion
extra["sqliteVersion"] = sqliteVersion
extra["mysqlVersion"] = mysqlVersion
extra["postgresVersion"] = postgresVersion
extra["h2Version"] = h2Version
extra["libbyVersion"] = libbyVersion
extra["bcryptVersion"] = bcryptVersion
extra["adventureVersion"] = adventureVersion
extra["minimessageVersion"] = minimessageVersion
extra["bukkitPlatformAdventure"] = bukkitPlatformAdventure
extra["zxingVersion"] = zxingVersion
extra["googleauthVersion"] = googleauthVersion
extra["floodgateVersion"] = floodgateVersion
extra["bstatsVersion"] = bstatsVersion
extra["mavenModelVersion"] = mavenModelVersion

subprojects {
    apply(plugin = "java")
    group = "net.loginto"
    version = "3.7.2"
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.alessiodp.com/releases")
        maven("https://repo.helpch.at/releases/")
        maven("https://repo.opencollab.dev/main/")
    }

    dependencies {
        implementation("net.byteflux:libby-bukkit:${rootProject.extra["libbyVersion"]}")
        implementation("net.byteflux:libby-bungee:${rootProject.extra["libbyVersion"]}")
        implementation("net.byteflux:libby-velocity:${rootProject.extra["libbyVersion"]}")
        implementation("org.mindrot:jbcrypt:${rootProject.extra["bcryptVersion"]}")
    }
}

repositories {
    mavenCentral()
    maven("https://repo.alessiodp.com/releases")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":bukkit", "shadow"))
    implementation(project(":bungeecord", "shadow"))
    implementation(project(":velocity"))

    implementation("net.byteflux:libby-bukkit:${rootProject.extra["libbyVersion"]}")
    implementation("net.byteflux:libby-bungee:${rootProject.extra["libbyVersion"]}")
    implementation("net.byteflux:libby-velocity:${rootProject.extra["libbyVersion"]}")
    implementation("org.mindrot:jbcrypt:${rootProject.extra["bcryptVersion"]}")
}

tasks {
    jar { enabled = false }


    shadowJar {
        archiveBaseName.set("LoginTo")
        archiveVersion.set("3.7.2")
        archiveClassifier.set("")

        relocate("net.byteflux.libby", "net.loginto.libs.libby")
        relocate("org.mindrot.jbcrypt", "net.loginto.libs.jbcrypt")
        relocate("org.bstats", "net.loginto.libs.bstats")
        relocate("com.zaxxer.hikari", "net.loginto.libs.hikari")
        relocate("com.mysql", "net.loginto.libs.mysql")
        relocate("org.postgresql", "net.loginto.libs.postgresql")
        relocate("org.h2", "net.loginto.libs.h2")
        relocate("com.google.zxing", "net.loginto.libs.zxing")
        relocate("com.warrenstrange.googleauth", "net.loginto.libs.googleauth")
        relocate("org.apache.maven.model", "net.loginto.libs.maven.model")
        relocate("org.codehaus.plexus.util", "net.loginto.libs.plexus.util")

    }

    build { dependsOn(shadowJar) }

}

tasks {
    runServer {
        minecraftVersion(minecraftTestServerVersion)

        val toolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(
            toolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(minecraftTestServerJversion))
            }
        )
    }
}