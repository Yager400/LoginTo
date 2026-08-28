import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("xyz.jpenilla.run-waterfall") version "3.0.2"
    id("xyz.jpenilla.run-velocity") version "3.0.2"
    `maven-publish`
}

val LoginToVersion = "4.0.0"

val javaVersion = JavaVersion.VERSION_17

allprojects {
    group = "com.github.yager400.loginto"
    version = LoginToVersion
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

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
        maven("https://jitpack.io")
    }

    dependencies {
        implementation("net.byteflux:libby-bukkit:1.3.1")
        implementation("net.byteflux:libby-bungee:1.3.1")
        implementation("net.byteflux:libby-velocity:1.3.1")
        implementation("org.mindrot:jbcrypt:0.4")
    }

    tasks.withType<ProcessResources>().configureEach {
        val ver = project.version.toString()
        inputs.property("version", ver)
        filesMatching("**/*.*") {
            expand("version" to ver)
        }
    }
}

repositories {
    mavenCentral()
    maven("https://repo.alessiodp.com/releases")
}

dependencies {
    implementation(project(":common", "shadow"))
    implementation(project(":folia-lib"))
    implementation(project(":bukkit", "shadow"))
    implementation(project(":bungeecord", "shadow"))
    implementation(project(":velocity", "shadow"))

    implementation("net.byteflux:libby-bukkit:1.3.1")
    implementation("net.byteflux:libby-bungee:1.3.1")
    implementation("net.byteflux:libby-velocity:1.3.1")
    implementation("org.mindrot:jbcrypt:0.4")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("**/*.*") {
        expand("version" to project.version)
    }
}

tasks {
    jar { enabled = false }
    
    shadowJar {
        archiveBaseName.set("LoginTo")
        archiveVersion.set(LoginToVersion)
        archiveClassifier.set("")

        relocate("net.byteflux.libby", "com.github.yager400.loginto.libs.libby")
        relocate("org.mindrot.jbcrypt", "com.github.yager400.loginto.libs.jbcrypt")
        relocate("org.bstats", "com.github.yager400.loginto.libs.bstats")
        relocate("com.zaxxer.hikari", "com.github.yager400.loginto.libs.hikari")
        relocate("com.mysql", "com.github.yager400.loginto.libs.mysql")
        relocate("org.postgresql", "com.github.yager400.loginto.libs.postgresql")
        relocate("org.h2", "com.github.yager400.loginto.libs.h2")
        relocate("com.google.zxing", "com.github.yager400.loginto.libs.zxing")
        relocate("com.warrenstrange.googleauth", "com.github.yager400.loginto.libs.googleauth")
        relocate("org.apache.maven.model", "com.github.yager400.loginto.libs.maven.model")
        relocate("org.codehaus.plexus.util", "com.github.yager400.loginto.libs.plexus.util")
        relocate("org.apache", "com.github.yager400.loginto.libs.apache")
        relocate("org.yaml.snakeyaml", "com.github.yager400.loginto.libs.yaml")

        dependsOn(processResources)
    }

    build {
        dependsOn(shadowJar)
    }

}

tasks {
    runServer {
        minecraftVersion("26.1.2")
        runDirectory(file("run-paper"))
        val toolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(
            toolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        )
    }
    runPaper.folia.registerTask {
        runDirectory(file("run-folia"))
        minecraftVersion("26.1.2")
        val toolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(
            toolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        )
    }
    runWaterfall {
        waterfallVersion("1.21")
        runDirectory(file("run-waterfall"))
    }
    runVelocity {
        velocityVersion("4.1.0")
        runDirectory(file("run-velocity"))
        val toolchains = project.extensions.getByType<JavaToolchainService>()
        javaLauncher.set(
            toolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        )
    }
}