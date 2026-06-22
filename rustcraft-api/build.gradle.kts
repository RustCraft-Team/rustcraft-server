plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

base {
    archivesName.set("rustcraft-api")
}

dependencies {
    api(libs.jackson.databind)
    api(libs.jackson.module.kotlin)
}
