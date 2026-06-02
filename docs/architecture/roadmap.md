# RustCraft Development Roadmap

## Phase 0 — Architecture & Repository Bootstrap

**Цель:** подготовить монорепозиторий без игрового кода.

- Создать архитектурную документацию.
- Спроектировать структуру Gradle-монорепозитория.
- Создать базовые Fabric/Kotlin модули.
- Зафиксировать правило: игровые модули взаимодействуют только через `rustcraft-api`.
- Добавить JSON Schema и default JSON config для каждого модуля.

**Exit criteria:** проект успешно конфигурируется Gradle, структура модулей создана, roadmap утвержден.

## Phase 1 — API Contracts

**Цель:** описать стабильные контракты без реализации игровой логики.

- Module lifecycle contracts.
- Event bus contracts.
- Service registry contracts.
- Configuration contracts.
- Permission/capability contracts.
- Persistence and networking contracts.
- Economy, Building, Loot and Raid domain contracts without gameplay implementation.
- Common identifiers and value objects.
- Domain model for Player, Clan, Building, Loot, Inventory, Raid, Vehicle, NPC, Monument, Safe Zone and Economy Account entities.
- JSON data contracts, schema versioning, indexes and migration rules for persisted domain entities.

**Exit criteria:** игровые модули могут проектироваться против `rustcraft-api` без прямых зависимостей друг от друга.

## Phase 2 — Building MVP

**Приоритет:** 1.

- Tool cupboard authorization model.
- Building block taxonomy.
- Upgrade tiers.
- Door/lock ownership contracts.
- Stability model.
- Upkeep configuration.

**Exit criteria:** базовая база игрока может быть построена, защищена и обслуживаться.

## Phase 3 — Loot MVP

**Приоритет:** 2.

- Loot table model.
- Barrels and crates.
- Monument loot profiles.
- Progression tiers.
- Recycler planning.

**Exit criteria:** игрок получает progression loop через исследование мира и loot sources.

## Phase 4 — Raiding MVP

**Приоритет:** 3.

- Explosive taxonomy.
- Structure durability model.
- Raid damage rules.
- Raid logging.
- Offline/online raid policy configuration.

**Exit criteria:** building и loot loop получают конфликтный слой с понятными правилами разрушения.

## Phase 5 — NPC MVP

**Приоритет:** 4.

- Scientist profiles.
- Animal profiles.
- Spawn rules.
- Aggression and patrol states.
- Monument NPC integration contracts.

**Exit criteria:** мир получает PvE-угрозы и охрану ключевых точек интереса.

## Phase 6 — Transport MVP

**Приоритет:** 5.

- Vehicle ownership.
- Fuel rules.
- Decay rules.
- Boat/ground vehicle abstractions.
- Storage and lock planning.

**Exit criteria:** игроки получают базовую мобильность с балансируемыми затратами.

## Phase 7 — Economy MVP

**Приоритет:** 6.

Экономика не выделена в отдельный Alpha-модуль на текущем этапе. До появления самостоятельного модуля economy-контракты должны проектироваться в `rustcraft-api`, а интеграции — через `rustcraft-loot`, `rustcraft-admin` и будущие торговые/магазинные сервисы.

- Currency/item value contracts.
- Vendor interaction contracts.
- Price configuration schema.
- Admin economic diagnostics.

**Exit criteria:** экономика может быть добавлена без переписывания loot/progression слоя.

## Phase 8 — World MVP

**Приоритет:** 7.

- Resource node distribution.
- Monument placement rules.
- Road and path planning.
- Spawn regions.
- Wipe generation profiles.

**Exit criteria:** серверный мир поддерживает Rust-like маршруты исследования, добычи и конфликтов.

## Long-term roadmap

- Расширение API для сторонних интеграций.
- Балансировка через data-driven JSON packs.
- Observability dashboard для администраторов.
- Автоматические тесты баланса loot/raid/building.
- Поддержка крупных wipe-серверов и профилирование производительности.
- Электричество после Alpha, если core loop стабилен.
