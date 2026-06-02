plugins {
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

group = "team.rustcraft"
version = "0.1.0-alpha.0"

allprojects {
    group = rootProject.group
    version = rootProject.version
}
