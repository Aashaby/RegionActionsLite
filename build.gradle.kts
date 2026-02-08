import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "2.0.30"
    id("org.jetbrains.kotlin.jvm") version "1.8.22"
}

taboolib {

    env {
        install(
            Bukkit,
            Kether,
            Basic,
            BukkitHook,
            BukkitNMS,
            BukkitNMSUtil,
            BukkitUtil,
            BukkitNavigation,
            I18n,
            JavaScript,
            Jexl,
            MinecraftChat,
            MinecraftEffect
        )
    }

    version {
        taboolib = "6.2.4-44a69de"
    }
}

repositories {
    mavenCentral()
    maven(url = "https://repo.tabooproject.org/repository/releases")
    maven(url = "https://repo.tabooproject.org/repository/snapshots")
}

dependencies {
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

configurations.configureEach {
    resolutionStrategy {
        cacheDynamicVersionsFor(0, "seconds")
        cacheChangingModulesFor(0, "seconds")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}