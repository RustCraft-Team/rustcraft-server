pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
    }
}

rootProject.name = "rustcraft-server"

include(
    ":rustcraft-api",
    ":rustcraft-building",
    ":rustcraft-survival",
    ":rustcraft-raids",
    ":rustcraft-loot",
    ":rustcraft-mobs",
    ":rustcraft-transport",
    ":rustcraft-world",
    ":rustcraft-admin",
    ":rustcraft-events",
    ":rustcraft-ui",
)
