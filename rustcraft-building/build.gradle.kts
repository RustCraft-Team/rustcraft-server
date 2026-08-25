plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

base {
    archivesName.set("rustcraft-building")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.language.kotlin)
    implementation(project(":rustcraft-api"))
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
}
