plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

base {
    archivesName.set("rustcraft-ui")
}

dependencies {
    implementation(project(":rustcraft-api"))
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
}
