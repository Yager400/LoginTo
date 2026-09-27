plugins {
    java
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.1"
}

dependencies {

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "loginto-api"
            from(components["java"])
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.shadowJar {

}