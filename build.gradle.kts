import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
}
val javaVersion = JavaVersion.VERSION_17
val spigotVersion       = "1.16.5-R0.1-SNAPSHOT"
val velocityVersion     = "3.4.0-SNAPSHOT"
val bungeeVersion       = "1.20-R0.1-SNAPSHOT"
val hikariVersion       = "4.0.3"
val packetEventsVersion = "2.12.0"
val placeholderApiVersion = "2.12.2"
val sqliteVersion       = "3.51.0.0"
val mysqlVersion        = "8.2.0"
val postgresVersion     = "42.7.8"
val h2Version           = "2.4.240"
val libbyVersion        = "1.3.1"
val bcryptVersion       = "0.4"
val adventureVersion    = "4.26.1"
val zxingVersion        = "3.5.3"
val googleauthVersion   = "1.5.0"

extra["spigotVersion"]        = spigotVersion
extra["velocityVersion"]      = velocityVersion
extra["bungeeVersion"]        = bungeeVersion
extra["hikariVersion"]        = hikariVersion
extra["packetEventsVersion"]  = packetEventsVersion
extra["placeholderApiVersion"]= placeholderApiVersion
extra["sqliteVersion"]        = sqliteVersion
extra["mysqlVersion"]         = mysqlVersion
extra["postgresVersion"]      = postgresVersion
extra["h2Version"]            = h2Version
extra["libbyVersion"]         = libbyVersion
extra["bcryptVersion"]        = bcryptVersion
extra["adventureVersion"]     = adventureVersion
extra["zxingVersion"]         = zxingVersion
extra["googleauthVersion"]    = googleauthVersion
subprojects {
    apply(plugin = "java")
    group = "net.loginto.bukkit"
    version = "3.5.0"
    java {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.alessiodp.com/releases")
        maven("https://repo.helpch.at/releases/")
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
    implementation(project(":bukkit"))
    implementation(project(":bungeecord"))
    implementation(project(":velocity"))

    implementation("net.byteflux:libby-bukkit:${rootProject.extra["libbyVersion"]}")
    implementation("net.byteflux:libby-bungee:${rootProject.extra["libbyVersion"]}")
    implementation("net.byteflux:libby-velocity:${rootProject.extra["libbyVersion"]}")
    implementation("org.mindrot:jbcrypt:${rootProject.extra["bcryptVersion"]}")
    //implementation("com.warrenstrange:googleauth:${rootProject.extra["googleauthVersion"]}")
}

tasks {
    jar { enabled = false }

    shadowJar {
        archiveBaseName.set("LoginTo")
        archiveVersion.set("3.5.0")
        archiveClassifier.set("")

        relocate("net.byteflux.libby", "net.loginto.libs.libby")
        relocate("org.mindrot.jbcrypt", "net.loginto.libs.jbcrypt")
    }

    build { dependsOn(shadowJar) }
}