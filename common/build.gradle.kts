plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {
    compileOnly("com.zaxxer:HikariCP:4.0.3")
    compileOnly("org.xerial:sqlite-jdbc:3.51.0.0")
    compileOnly("com.mysql:mysql-connector-j:8.2.0")
    compileOnly("org.yaml:snakeyaml:2.2")
    compileOnly("org.apache.maven:maven-model:3.9.9")
    compileOnly("com.google.code.gson:gson:2.10.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {
    relocate("org.yaml", "com.github.yager400.loginto.libs.yaml")
}