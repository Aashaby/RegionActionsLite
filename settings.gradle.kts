pluginManagement {
    repositories {
        maven(url = "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven(url = "https://repo.tabooproject.org/repository/releases")
        maven(url = "https://repo.tabooproject.org/repository/snapshots")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven(url = "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven(url = "https://repo.tabooproject.org/repository/releases")
        maven(url = "https://repo.tabooproject.org/repository/snapshots")
        mavenCentral()
    }
}

rootProject.name="RegionActionsLite"