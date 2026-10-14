plugins {
    java
    `java-library`
    `maven-publish`
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