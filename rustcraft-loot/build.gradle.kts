plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

base {
    archivesName.set("rustcraft-loot")
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

    implementation(project(":rustcraft-api"))
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
