# RustCraft Architecture

## 1. Назначение проекта

RustCraft — серверный Fabric-проект для Minecraft Java Edition 1.20.1, цель которого — максимально близко воспроизвести игровой цикл Rust внутри Minecraft без копирования декоративного, праздничного и скин-контента. Проект разрабатывается как долгоживущий модульный монорепозиторий организации `RustCraft-Team` с главным репозиторием `rustcraft-server`.

Документ фиксирует архитектуру до написания игрового кода. На текущем этапе допустимы только проектная структура, Gradle-конфигурация, JSON-ресурсы конфигурации и документация.

## 2. Архитектурные принципы

- **Модульность:** каждая игровая область изолирована в отдельном Gradle/Fabric-модуле.
- **API-first:** игровые модули взаимодействуют друг с другом только через `rustcraft-api`.
- **SOLID:** контракты, сервисы, конфигурации и обработчики проектируются с узкими ответственностями и зависимостями от абстракций.
- **Server-first:** целевая среда всех модулей — сервер; клиентская логика не вводится без отдельного архитектурного решения.
- **JSON-first configuration:** все будущие конфигурации, дефолтные значения и схемы валидируются через JSON Schema.
- **Production-ready:** структура проекта сразу поддерживает тесты, версионирование, Gradle Kotlin DSL, кэширование сборки и независимую эволюцию модулей.
- **No direct module coupling:** `rustcraft-building` не зависит от `rustcraft-loot`, `rustcraft-raids` не зависит от `rustcraft-building` и т. д.; общие типы и события выносятся в `rustcraft-api`.

## 3. Технологический стек

The official platform baseline and compatibility policy are defined in [Target Platform](platform.md). All module build files must inherit Minecraft/Fabric dependency versions from the shared Gradle version catalog.

| Область | Решение |
| --- | --- |
| Minecraft | Java Edition 1.20.1 |
| Mod loader | Fabric Loader |
| API | Fabric API |
| Язык | Kotlin + Java interoperability |
| JVM | Java 21 toolchain |
| Build system | Gradle Kotlin DSL |
| Конфигурации | JSON + JSON Schema Draft 2020-12 |
| Организация | RustCraft-Team |
| Репозиторий | rustcraft-server |

## 4. Структура монорепозитория

```text
rustcraft-server/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml
├── docs/
│   └── architecture/
│       ├── README.md
│       ├── platform.md
│       ├── rustcraft-api.md
│       ├── domain-model.md
│       ├── data-contracts.md
│       └── roadmap.md
├── rustcraft-api/
├── rustcraft-building/
├── rustcraft-survival/
├── rustcraft-raids/
├── rustcraft-loot/
├── rustcraft-mobs/
├── rustcraft-transport/
├── rustcraft-world/
├── rustcraft-admin/
├── rustcraft-events/
└── rustcraft-ui/
```

Каждый модуль имеет одинаковую базовую структуру:

```text
<module>/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── java/
    │   ├── kotlin/
    │   └── resources/
    │       ├── fabric.mod.json
    │       └── rustcraft/<module>/
    │           ├── config.default.json
    │           └── config.schema.json
    └── test/
        ├── kotlin/
        └── resources/
```

Папки `java` и `kotlin` подготовлены, но игровой код на этом этапе не добавляется.

## 5. Слои архитектуры

### 5.1 `rustcraft-api`

Единственный модуль, через который игровые модули обмениваются контрактами. В будущем здесь должны находиться:

- публичные интерфейсы сервисов;
- event contracts;
- immutable DTO/value objects;
- JSON configuration contracts;
- registry abstractions;
- capability/permission abstractions;
- stable extension points для сторонних модулей.

`rustcraft-api` не должен содержать бизнес-логику конкретной игровой механики. Детальная архитектура API, включая Event Bus, Service Registry, Lifecycle, Capability, Configuration, Persistence, Networking, Economy, Building, Loot и Raid contracts, описана в [RustCraft API Architecture](rustcraft-api.md). Основные доменные сущности, ownership, serialization и persistence boundaries описаны в [Domain Model](domain-model.md). JSON data contracts, schema versioning, indexes and migration rules are described in [Data Contracts](data-contracts.md).

### 5.2 Игровые модули

Игровые модули реализуют конкретные механики и зависят только от `rustcraft-api`, Fabric Loader/API, Fabric Language Kotlin и JSON-библиотек. Любой обмен состоянием, событиями или командами между модулями проходит через контракты API.

### 5.3 Конфигурационный слой

Каждый модуль имеет:

- `config.default.json` — дефолтная конфигурация модуля;
- `config.schema.json` — JSON Schema для валидации;
- будущий loader/validator должен быть вынесен в `rustcraft-api` как контракт и реализован инфраструктурным сервисом.

## 6. Модули и зоны ответственности

| Модуль | Ответственность |
| --- | --- |
| `rustcraft-api` | Контракты, события, DTO, конфигурационные интерфейсы, extension points |
| `rustcraft-building` | Building privilege, foundations, walls, doors, locks, tool cupboard, stability, upkeep |
| `rustcraft-survival` | Health, hunger, hydration, temperature, wounds, respawn, sleeping bags/beds |
| `rustcraft-raids` | Explosives, raid damage, structure durability, raid logging, breach rules |
| `rustcraft-loot` | Loot tables, barrels, crates, monuments, recyclers, progression gates |
| `rustcraft-mobs` | Scientists, animals, patrol AI, aggression profiles, NPC spawning |
| `rustcraft-transport` | Boats, vehicles, fuel, decay, ownership, future vehicle modules |
| `rustcraft-world` | Map rules, monuments, roads, resources, biomes, spawn distribution |
| `rustcraft-admin` | Admin commands, moderation, diagnostics, observability, permissions |
| `rustcraft-events` | Wipes, timed server events, lifecycle orchestration, scheduled events |
| `rustcraft-ui` | HUD, overlays, notifications, menus, localization keys |

## 7. Dependency rules

Разрешенная зависимость:

```text
rustcraft-<game-module> ──▶ rustcraft-api
```

Запрещенные зависимости:

```text
rustcraft-building ──X──▶ rustcraft-loot
rustcraft-raids    ──X──▶ rustcraft-building
rustcraft-ui       ──X──▶ rustcraft-survival
```

Если модулю требуется действие другого модуля, он публикует или потребляет контракт API:

1. `rustcraft-api` объявляет событие/интерфейс.
2. Модуль-источник публикует событие через API bus.
3. Модуль-потребитель подписывается через API bus.
4. Ни один модуль не импортирует классы другого игрового модуля.

## 8. Alpha feature boundaries

В Alpha включаются только системы, которые нужны для базового Rust-like survival loop:

- Building;
- Loot;
- Raiding;
- NPC;
- Transport;
- Economy;
- World.

Из Alpha исключаются:

- система скинов;
- праздничные события;
- декоративный контент;
- электричество;
- контент, не влияющий на core survival/raid/progression loop.

## 9. Production-readiness требования

- Все публичные контракты документируются до реализации.
- Каждая фича должна иметь JSON-конфигурацию и schema-файл.
- Межмодульные зависимости проверяются на уровне Gradle и code review.
- Игровые модули не должны иметь mutable global state без lifecycle ownership.
- Все event handlers должны быть идемпотентными там, где это возможно.
- Ошибки конфигурации должны давать понятные сообщения и safe fallback.
- Версионирование API должно быть совместимо с multi-year развитием проекта.

## 10. План следующего проектного шага

1. Описать контракты `rustcraft-api` без реализации игровой логики.
2. Создать ADR для event bus, configuration loader и module lifecycle.
3. Добавить Gradle convention plugin для устранения дублирования build scripts.
4. Добавить тест на запрет прямых зависимостей между игровыми модулями.
5. Поддерживать `platform.md` при любом изменении Minecraft/Fabric/Java/Gradle baseline.
6. После утверждения API начать реализацию `rustcraft-building` как первого приоритета.
