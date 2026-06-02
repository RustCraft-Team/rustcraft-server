plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

base {
    archivesName.set("rustcraft-api")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.fabric.loader)
    compileOnly(libs.fabric.api)
    compileOnly(libs.fabric.language.kotlin)

    api(libs.jackson.databind)
    api(libs.jackson.module.kotlin)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
