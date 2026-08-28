plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    implementation(project(":common"))
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1")
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("com.google.zxing:core:3.5.3")
    compileOnly("org.xerial:sqlite-jdbc:3.51.0.0")
    compileOnly("com.mysql:mysql-connector-j:8.2.0")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.26.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.26.1")
    compileOnly("net.kyori:adventure-platform-bungeecord:4.3.4")
    implementation("org.bstats:bstats-bungeecord:3.2.1")
}

tasks.shadowJar {
    relocate("net.kyori", "com.github.yager400.loginto.libs.kyori")
}