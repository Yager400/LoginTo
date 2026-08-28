plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("com.google.zxing:core:3.5.3")
    compileOnly("org.xerial:sqlite-jdbc:3.51.0.0")
    compileOnly("com.mysql:mysql-connector-j:8.2.0")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    implementation("org.bstats:bstats-velocity:3.2.1")
}