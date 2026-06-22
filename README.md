# RustCraft Server

RustCraft — серверный монорепозиторий организации `RustCraft-Team` для Fabric-модов Minecraft Java Edition 1.20.1. Цель проекта — долгосрочная модульная реализация Rust-like survival, building, loot, raiding, NPC, transport, economy и world systems внутри Minecraft.

На текущем этапе игровой код намеренно не добавляется: репозиторий содержит архитектурную документацию, Gradle Kotlin DSL структуру и JSON-ready конфигурационные ресурсы модулей.

## Документация

- [Архитектура](docs/architecture/README.md)
- [RustCraft API Architecture](docs/architecture/rustcraft-api.md)
- [Domain Model](docs/architecture/domain-model.md)
- [Data Contracts](docs/architecture/data-contracts.md)
- [Roadmap](docs/architecture/roadmap.md)
- [Target Platform](docs/architecture/platform.md)

## Модули

- `rustcraft-api`
- `rustcraft-building`
- `rustcraft-survival`
- `rustcraft-raids`
- `rustcraft-loot`
- `rustcraft-mobs`
- `rustcraft-transport`
- `rustcraft-world`
- `rustcraft-admin`
- `rustcraft-events`
- `rustcraft-ui`

## Ключевое правило зависимостей

Все игровые модули зависят только от `rustcraft-api` и не импортируют классы друг друга напрямую.

## Target platform

RustCraft officially targets Minecraft Java Edition `1.20.1` on Fabric Loader + Fabric API, built with Java `21` and Gradle Kotlin DSL. All module build files inherit Minecraft/Fabric dependency versions from the shared Gradle version catalog; per-module platform version overrides are not supported. See [Target Platform](docs/architecture/platform.md) for the compatibility policy.

## Технологии

- Minecraft Java Edition 1.20.1
- Fabric Loader
- Fabric API
- Fabric Language Kotlin
- Java 21 toolchain
- Kotlin
- Gradle Kotlin DSL
- JSON Schema Draft 2020-12
