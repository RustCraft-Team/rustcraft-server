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

subprojects {
    plugins.withId("java-library") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(21))
            withSourcesJar()
        }

        dependencies {
            "compileOnly"(libs.fabric.loader)
            "compileOnly"(libs.fabric.api)
            "compileOnly"(libs.fabric.language.kotlin)
            "testImplementation"(libs.junit.jupiter)
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
}
