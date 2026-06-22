# RustCraft Target Platform

RustCraft has one official target platform for architecture, build configuration, testing and future implementation work. The project does not currently support a platform matrix.

## Supported platform

| Area | Supported version / technology | Policy |
| --- | --- | --- |
| Minecraft | Minecraft Java Edition `1.20.1` | The only supported game version for Alpha architecture and module foundations. |
| Mod loader | Fabric Loader | All modules are Fabric modules and must not introduce Forge, NeoForge, Paper, Bukkit or Spigot dependencies. |
| Mod API | Fabric API | Shared Minecraft-facing integrations must use Fabric API through the common Gradle version catalog. |
| JVM | Java `21` | All Gradle modules inherit the Java 21 toolchain from the root build. |
| Build DSL | Gradle Kotlin DSL | Build scripts must use `*.gradle.kts`; Groovy Gradle scripts are not part of the supported foundation. |
| Dependency versions | Gradle version catalog | Minecraft, Yarn, Fabric Loader, Fabric API and Fabric Language Kotlin versions are centralized in `gradle/libs.versions.toml`. |

## Compatibility policy

- **Single-version target:** RustCraft targets Minecraft `1.20.1` only until an explicit architecture decision changes this document.
- **Centralized upgrades:** Minecraft and Fabric dependency upgrades must be performed by changing the version catalog, then validating every Gradle module against the same catalog values.
- **No per-module Minecraft drift:** A module must not pin its own Minecraft, Fabric Loader or Fabric API version outside the shared catalog.
- **Server-first foundation:** Current architecture remains server-oriented. Client-only behavior requires a separate architecture decision before implementation.
- **No gameplay implementation in platform work:** Platform changes may update build files, documentation, version catalogs and module foundations only; they must not add gameplay logic or Minecraft content registration.
- **Migration requirement:** Any future move away from Minecraft `1.20.1`, Fabric Loader, Fabric API, Java 21 or Gradle Kotlin DSL must include an architecture document update, roadmap update and compatibility notes before implementation work starts.

## Build inheritance

The root Gradle build owns common module configuration:

1. every subproject uses Java 21 toolchains;
2. every subproject uses the shared Fabric Loader, Fabric API and Fabric Language Kotlin aliases from the version catalog;
3. every test task uses JUnit Platform;
4. module build files only declare module-specific dependencies and archive names.

This keeps the target Minecraft/Fabric platform fixed across `rustcraft-api` and all gameplay-area modules without adding gameplay behavior.
