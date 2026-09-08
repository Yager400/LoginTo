plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":folia-lib"))
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.13.0")
    compileOnly("me.clip:placeholderapi:2.12.2")
    implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")
    implementation("net.kyori:adventure-text-minimessage:4.26.1")
    implementation("net.kyori:adventure-nbt:4.26.1")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.warrenstrange:googleauth:1.5.0")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("com.zaxxer:HikariCP:4.0.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    relocate("net.kyori", "com.github.yager400.loginto.libs.kyori")
}