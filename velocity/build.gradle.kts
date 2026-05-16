dependencies {
    compileOnly("com.velocitypowered:velocity-api:${rootProject.extra["velocityVersion"]}")
    compileOnly("com.zaxxer:HikariCP:${rootProject.extra["hikariVersion"]}")
    compileOnly("net.kyori:adventure-api:${rootProject.extra["adventureVersion"]}")
    compileOnly("com.google.zxing:core:${rootProject.extra["zxingVersion"]}")
    compileOnly("org.xerial:sqlite-jdbc:${rootProject.extra["sqliteVersion"]}")
    compileOnly("com.mysql:mysql-connector-j:${rootProject.extra["mysqlVersion"]}")
    compileOnly("org.postgresql:postgresql:${rootProject.extra["postgresVersion"]}")
    compileOnly("com.h2database:h2:${rootProject.extra["h2Version"]}")
}