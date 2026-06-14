dependencies {
    compileOnly("com.zaxxer:HikariCP:${rootProject.extra["hikariVersion"]}")
    compileOnly("org.xerial:sqlite-jdbc:${rootProject.extra["sqliteVersion"]}")
    compileOnly("com.mysql:mysql-connector-j:${rootProject.extra["mysqlVersion"]}")
    compileOnly("org.postgresql:postgresql:${rootProject.extra["postgresVersion"]}")
    compileOnly("com.h2database:h2:${rootProject.extra["h2Version"]}")
    compileOnly("org.yaml:snakeyaml:2.2")
    compileOnly("org.apache.maven:maven-model:${rootProject.extra["mavenModelVersion"]}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}