# RustCraft JSON Data Contracts

## 1. Назначение документа

Этот документ проектирует JSON Data Contracts для основных сущностей RustCraft. Он не содержит реализации игрового кода, механик, предметов, блоков или регистрации Minecraft-контента. Все схемы являются архитектурными контрактами для будущего `rustcraft-api`, persistence-слоя, миграций, admin tooling и data-driven конфигураций.

Документ описывает:

- JSON Schema для ключевых сущностей;
- версионирование contract-схем;
- правила миграции;
- обязательные и опциональные поля;
- индексы;
- persistence scope;
- примеры JSON документов;
- правила обратной совместимости.

## 2. Общий envelope для persistence-документов

Все persisted domain documents должны использовать единый envelope. Конкретная сущность хранит domain-state внутри `state`, а общие поля остаются одинаковыми для всех типов.

### 2.1 Base JSON Schema

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/common/entity-envelope.schema.json",
  "title": "RustCraft Entity Envelope",
  "type": "object",
  "additionalProperties": false,
  "required": [
    "schemaVersion",
    "entityType",
    "entityId",
    "persistenceScope",
    "state",
    "metadata"
  ],
  "properties": {
    "schemaVersion": {
      "type": "integer",
      "minimum": 1
    },
    "entityType": {
      "type": "string",
      "pattern": "^rustcraft:[a-z0-9_.-]+/[a-z0-9_.-]+$"
    },
    "entityId": {
      "type": "string",
      "minLength": 1
    },
    "persistenceScope": {
      "type": "string",
      "enum": [
        "SERVER_GLOBAL",
        "WIPE_WORLD",
        "PLAYER_PROFILE",
        "CLAN_PROFILE",
        "REGION",
        "TRANSIENT_SESSION"
      ]
    },
    "state": {
      "type": "object"
    },
    "refs": {
      "type": "object",
      "additionalProperties": {
        "type": ["string", "array", "object", "null"]
      }
    },
    "metadata": {
      "$ref": "#/$defs/metadata"
    }
  },
  "$defs": {
    "metadata": {
      "type": "object",
      "additionalProperties": false,
      "required": ["createdAt", "updatedAt", "sourceModule"],
      "properties": {
        "createdAt": { "type": "string", "format": "date-time" },
        "updatedAt": { "type": "string", "format": "date-time" },
        "sourceModule": { "type": "string" },
        "correlationId": { "type": "string" },
        "migration": { "$ref": "#/$defs/migrationMetadata" },
        "tags": {
          "type": "array",
          "items": { "type": "string" },
          "uniqueItems": true
        }
      }
    },
    "migrationMetadata": {
      "type": "object",
      "additionalProperties": false,
      "required": ["fromVersion", "toVersion", "migratedAt"],
      "properties": {
        "fromVersion": { "type": "integer", "minimum": 1 },
        "toVersion": { "type": "integer", "minimum": 1 },
        "migratedAt": { "type": "string", "format": "date-time" },
        "migrationId": { "type": "string" }
      }
    }
  }
}
```

### 2.2 Общие обязательные поля

| Поле | Назначение |
| --- | --- |
| `schemaVersion` | Версия схемы конкретного документа. |
| `entityType` | Namespaced тип: например `rustcraft:player/profile`. |
| `entityId` | Стабильный идентификатор документа. |
| `persistenceScope` | Scope хранения. |
| `state` | Domain-state сущности. |
| `metadata` | Audit, timestamps, source module и migration metadata. |

### 2.3 Общие опциональные поля

| Поле | Назначение |
| --- | --- |
| `refs` | Ссылки на другие агрегаты без глубокого циклического embedding. |
| `metadata.correlationId` | Trace id операции, породившей изменение. |
| `metadata.migration` | Информация о последней миграции. |
| `metadata.tags` | Индексируемые технические или admin-теги. |

## 3. Версионирование data contracts

### 3.1 Schema version

- `schemaVersion` — integer внутри каждого документа.
- Версия относится к конкретному `entityType`, а не ко всему проекту.
- Начальная версия для всех сущностей — `1`.
- Версия увеличивается при изменении структуры `state`, обязательных полей или semantics существующих полей.

### 3.2 Совместимые изменения

Совместимыми считаются:

- добавление optional поля;
- добавление enum value только при наличии documented fallback;
- добавление нового индекса без изменения документа;
- расширение `metadata` optional-полями;
- добавление нового `refs`-ключа.

### 3.3 Несовместимые изменения

Несовместимыми считаются:

- удаление обязательного поля;
- переименование поля;
- изменение типа поля;
- изменение semantics существующего поля;
- изменение identity model;
- изменение persistence scope без migration plan;
- изменение default wipe policy.

## 4. Правила миграции между версиями

1. Каждая миграция должна иметь `migrationId` в формате `rustcraft:<entity>/<from>-to-<to>`.
2. Миграции выполняются последовательно: `1 -> 2 -> 3`, без пропуска промежуточных версий.
3. Миграция должна быть deterministic: одинаковый input дает одинаковый output.
4. Миграция не должна требовать загруженного Minecraft world или runtime-only объектов.
5. Миграция должна сохранять `entityId`, если ADR явно не разрешает re-keying.
6. Перед миграцией создается snapshot или backup согласно persistence policy.
7. При ошибке миграции старая версия документа остается доступной read-only, а модуль получает diagnostic failure.
8. Новые required поля должны иметь default derivation или explicit backfill rule.
9. Cross-aggregate migrations должны использовать refs и индексы, а не прямое глубокое изменение чужих aggregates без transaction boundary.
10. Wipe migrations и schema migrations разделяются: wipe не должен маскировать ошибку schema migration.

## 5. Индексирование

Индексы описываются архитектурно и не навязывают конкретную БД. Каждый persistence backend должен уметь построить эквивалентные индексы.

| Тип индекса | Назначение |
| --- | --- |
| `primary` | Быстрый lookup по `entityType + entityId`. |
| `owner` | Поиск объектов владельца: `ownerType + ownerId`. |
| `scope` | Фильтрация по `persistenceScope`. |
| `region` | World/region/chunk lookup. |
| `state` | Поиск активных, заблокированных, разрушенных, archived документов. |
| `updatedAt` | Incremental backup, sync и audit. |
| `tag` | Admin/debug queries. |
| `ref` | Поиск документов, ссылающихся на другой aggregate. |

## 6. Общие reusable definitions

Эти definitions должны быть переиспользованы entity-specific schemas.

```json
{
  "$defs": {
    "ownerRef": {
      "type": "object",
      "additionalProperties": false,
      "required": ["ownerType", "ownershipRole"],
      "properties": {
        "ownerType": {
          "type": "string",
          "enum": ["PLAYER", "CLAN", "SERVER", "SYSTEM", "NPC", "NONE"]
        },
        "ownerId": { "type": ["string", "null"] },
        "ownershipRole": {
          "type": "string",
          "enum": ["OWNER", "MEMBER", "AUTHORIZED", "CREATOR", "ADMIN", "SYSTEM_MANAGED"]
        },
        "validFrom": { "type": "string", "format": "date-time" },
        "validUntil": { "type": ["string", "null"], "format": "date-time" }
      }
    },
    "regionRef": {
      "type": "object",
      "additionalProperties": false,
      "required": ["worldId"],
      "properties": {
        "worldId": { "type": "string" },
        "regionId": { "type": "string" },
        "chunkX": { "type": "integer" },
        "chunkZ": { "type": "integer" },
        "boundsId": { "type": "string" }
      }
    },
    "itemQuantity": {
      "type": "integer",
      "minimum": 0
    },
    "durabilityState": {
      "type": "object",
      "additionalProperties": false,
      "required": ["current", "max"],
      "properties": {
        "current": { "type": "number", "minimum": 0 },
        "max": { "type": "number", "exclusiveMinimum": 0 }
      }
    }
  }
}
```

## 7. Entity contracts

### 7.1 Player

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:player/profile` |
| Persistence Scope | `PLAYER_PROFILE` |
| Version | `1` |
| Primary Index | `entityType + playerId` |
| Secondary Indexes | `state.lifecycle`, `state.clanMemberships.clanId`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/player/profile.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "type": "object",
      "properties": {
        "entityType": { "const": "rustcraft:player/profile" },
        "persistenceScope": { "const": "PLAYER_PROFILE" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["playerId", "lifecycle", "displayName", "createdAt"],
          "properties": {
            "playerId": { "type": "string" },
            "externalUuid": { "type": "string" },
            "lifecycle": { "enum": ["DISCOVERED", "PROFILE_CREATED", "ONLINE", "OFFLINE", "SUSPENDED"] },
            "displayName": { "type": "string" },
            "clanMemberships": { "type": "array", "items": { "type": "object" } },
            "inventoryRefs": { "type": "array", "items": { "type": "string" } },
            "equipmentRef": { "type": "string" },
            "economyAccountRefs": { "type": "array", "items": { "type": "string" } },
            "createdAt": { "type": "string", "format": "date-time" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`schemaVersion`, `entityType`, `entityId`, `persistenceScope`, `state.playerId`, `state.lifecycle`, `state.displayName`, `state.createdAt`, `metadata`.

#### Опциональные поля

`state.externalUuid`, `state.clanMemberships`, `state.inventoryRefs`, `state.equipmentRef`, `state.economyAccountRefs`, `refs`, `metadata.tags`.

#### Миграции

- `v1 -> v2`: допускается добавление privacy/settings snapshot с default values.
- Player identity migration требует отдельного ADR, потому что влияет на ownership и economy refs.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:player/profile",
  "entityId": "player-4f1c9d4a",
  "persistenceScope": "PLAYER_PROFILE",
  "state": {
    "playerId": "player-4f1c9d4a",
    "externalUuid": "00000000-0000-0000-0000-000000000001",
    "lifecycle": "OFFLINE",
    "displayName": "SurvivorOne",
    "clanMemberships": [
      { "clanId": "clan-01HZX", "role": "MEMBER" }
    ],
    "inventoryRefs": ["inventory-player-4f1c9d4a-main"],
    "equipmentRef": "equipment-player-4f1c9d4a",
    "economyAccountRefs": ["acct-player-4f1c9d4a-scrap"],
    "createdAt": "2026-06-02T00:00:00Z"
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.2 Clan

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:clan/profile` |
| Persistence Scope | `CLAN_PROFILE` |
| Version | `1` |
| Primary Index | `entityType + clanId` |
| Secondary Indexes | `state.members.playerId`, `state.lifecycle`, `state.display.tag`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/clan/profile.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:clan/profile" },
        "persistenceScope": { "const": "CLAN_PROFILE" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["clanId", "lifecycle", "display", "members"],
          "properties": {
            "clanId": { "type": "string" },
            "lifecycle": { "enum": ["CREATED", "ACTIVE", "DISBANDED", "ARCHIVED"] },
            "display": {
              "type": "object",
              "required": ["name"],
              "properties": {
                "name": { "type": "string" },
                "tag": { "type": "string" }
              }
            },
            "members": { "type": "array", "items": { "type": "object" } },
            "ownedObjectRefs": { "type": "array", "items": { "type": "string" } },
            "economyAccountRefs": { "type": "array", "items": { "type": "string" } }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.clanId`, `state.lifecycle`, `state.display.name`, `state.members`.

#### Опциональные поля

`state.display.tag`, `state.ownedObjectRefs`, `state.economyAccountRefs`.

#### Миграции

- При wipe migration ссылки на `WIPE_WORLD` objects в `ownedObjectRefs` удаляются или архивируются.
- Role enum extensions требуют fallback role `MEMBER` или explicit mapping.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:clan/profile",
  "entityId": "clan-01HZX",
  "persistenceScope": "CLAN_PROFILE",
  "state": {
    "clanId": "clan-01HZX",
    "lifecycle": "ACTIVE",
    "display": { "name": "Oxide", "tag": "OX" },
    "members": [
      { "playerId": "player-4f1c9d4a", "role": "OWNER", "joinedAt": "2026-06-02T00:00:00Z" }
    ],
    "ownedObjectRefs": ["building-01JBASE"],
    "economyAccountRefs": ["acct-clan-01HZX-scrap"]
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.3 Building

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:building/structure` |
| Persistence Scope | `WIPE_WORLD` |
| Version | `1` |
| Primary Index | `entityType + buildingId` |
| Secondary Indexes | `state.owner.ownerType + ownerId`, `state.region.regionId`, `state.lifecycle`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/building/structure.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:building/structure" },
        "persistenceScope": { "const": "WIPE_WORLD" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["buildingId", "lifecycle", "owner", "region", "blockRefs"],
          "properties": {
            "buildingId": { "type": "string" },
            "lifecycle": { "enum": ["PLANNED", "ACTIVE", "DECAYING", "RAID_DAMAGED", "COLLAPSED", "REMOVED"] },
            "owner": { "$ref": "common/defs.schema.json#/$defs/ownerRef" },
            "region": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "blockRefs": { "type": "array", "items": { "type": "string" } },
            "toolCupboardRefs": { "type": "array", "items": { "type": "string" } },
            "upkeepSnapshot": { "type": "object" },
            "raidExposure": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.buildingId`, `state.lifecycle`, `state.owner`, `state.region`, `state.blockRefs`.

#### Опциональные поля

`state.toolCupboardRefs`, `state.upkeepSnapshot`, `state.raidExposure`.

#### Миграции

- `blockRefs` must be rebuilt from region indexes if missing only during controlled recovery migration.
- Ownership changes require audit metadata and should not be silent schema migrations.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:building/structure",
  "entityId": "building-01JBASE",
  "persistenceScope": "WIPE_WORLD",
  "state": {
    "buildingId": "building-01JBASE",
    "lifecycle": "ACTIVE",
    "owner": {
      "ownerType": "CLAN",
      "ownerId": "clan-01HZX",
      "ownershipRole": "OWNER",
      "validFrom": "2026-06-02T00:00:00Z"
    },
    "region": { "worldId": "world-main", "regionId": "r.0.0" },
    "blockRefs": ["block-01JFOUNDATION"],
    "toolCupboardRefs": ["tc-01JTC"]
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.4 Building Block

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:building/block` |
| Persistence Scope | `WIPE_WORLD` |
| Version | `1` |
| Primary Index | `entityType + blockId` |
| Secondary Indexes | `state.buildingId`, `state.location.chunkX + chunkZ`, `state.tier`, `state.lifecycle` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/building/block.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:building/block" },
        "persistenceScope": { "const": "WIPE_WORLD" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["blockId", "buildingId", "lifecycle", "blockType", "tier", "durability", "location"],
          "properties": {
            "blockId": { "type": "string" },
            "buildingId": { "type": "string" },
            "lifecycle": { "enum": ["PLACED", "UPGRADED", "REPAIRED", "DAMAGED", "DESTROYED", "DECAYED"] },
            "blockType": { "type": "string" },
            "tier": { "type": "string" },
            "durability": { "$ref": "common/defs.schema.json#/$defs/durabilityState" },
            "stability": { "type": "object" },
            "location": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "creatorRef": { "$ref": "common/defs.schema.json#/$defs/ownerRef" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.blockId`, `state.buildingId`, `state.lifecycle`, `state.blockType`, `state.tier`, `state.durability`, `state.location`.

#### Опциональные поля

`state.stability`, `state.creatorRef`, graph-neighbor refs in `refs`.

#### Миграции

- New block tiers require fallback tier category for raid/building compatibility.
- Location format migration must preserve `blockId` and rebuild region indexes.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:building/block",
  "entityId": "block-01JFOUNDATION",
  "persistenceScope": "WIPE_WORLD",
  "state": {
    "blockId": "block-01JFOUNDATION",
    "buildingId": "building-01JBASE",
    "lifecycle": "PLACED",
    "blockType": "foundation",
    "tier": "stone",
    "durability": { "current": 500, "max": 500 },
    "location": { "worldId": "world-main", "regionId": "r.0.0", "chunkX": 0, "chunkZ": 0 }
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.5 Tool Cupboard

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:building/tool-cupboard` |
| Persistence Scope | `WIPE_WORLD` |
| Version | `1` |
| Primary Index | `entityType + toolCupboardId` |
| Secondary Indexes | `state.buildingId`, `state.authorized.playerId`, `state.authorized.clanId`, `state.scope.regionId` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/building/tool-cupboard.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:building/tool-cupboard" },
        "persistenceScope": { "const": "WIPE_WORLD" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["toolCupboardId", "buildingId", "lifecycle", "authorized", "scope"],
          "properties": {
            "toolCupboardId": { "type": "string" },
            "buildingId": { "type": "string" },
            "lifecycle": { "enum": ["PLACED", "AUTHORIZING", "UPKEEP_ACTIVE", "DAMAGED", "DESTROYED", "REMOVED"] },
            "authorized": {
              "type": "object",
              "properties": {
                "players": { "type": "array", "items": { "type": "string" } },
                "clans": { "type": "array", "items": { "type": "string" } }
              }
            },
            "scope": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "upkeepInventoryRef": { "type": "string" },
            "upkeepState": { "type": "object" },
            "accessPolicy": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.toolCupboardId`, `state.buildingId`, `state.lifecycle`, `state.authorized`, `state.scope`.

#### Опциональные поля

`state.upkeepInventoryRef`, `state.upkeepState`, `state.accessPolicy`.

#### Миграции

- Authorization entries migrate by stable player/clan ids only, never display names.
- Upkeep policy migrations must preserve safe fallback to prevent accidental decay from invalid migrated data.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:building/tool-cupboard",
  "entityId": "tc-01JTC",
  "persistenceScope": "WIPE_WORLD",
  "state": {
    "toolCupboardId": "tc-01JTC",
    "buildingId": "building-01JBASE",
    "lifecycle": "UPKEEP_ACTIVE",
    "authorized": {
      "players": ["player-4f1c9d4a"],
      "clans": ["clan-01HZX"]
    },
    "scope": { "worldId": "world-main", "regionId": "r.0.0" },
    "upkeepInventoryRef": "inventory-tc-01JTC"
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.6 Inventory

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:inventory/container` |
| Persistence Scope | Owner-dependent: `PLAYER_PROFILE`, `WIPE_WORLD`, `REGION`, or `TRANSIENT_SESSION` |
| Version | `1` |
| Primary Index | `entityType + inventoryId` |
| Secondary Indexes | `state.owner.ownerType + ownerId`, `state.inventoryType`, `state.lockState`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/inventory/container.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:inventory/container" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["inventoryId", "owner", "inventoryType", "slots", "lockState"],
          "properties": {
            "inventoryId": { "type": "string" },
            "owner": { "$ref": "common/defs.schema.json#/$defs/ownerRef" },
            "inventoryType": { "type": "string" },
            "slots": {
              "type": "array",
              "items": {
                "type": "object",
                "required": ["slotIndex"],
                "properties": {
                  "slotIndex": { "type": "integer", "minimum": 0 },
                  "slotType": { "type": "string" },
                  "itemStackRef": { "type": "string" },
                  "embeddedStack": { "type": "object" }
                }
              }
            },
            "constraints": { "type": "object" },
            "lockState": { "enum": ["UNLOCKED", "LOCKED", "MIGRATING", "ARCHIVED"] }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.inventoryId`, `state.owner`, `state.inventoryType`, `state.slots`, `state.lockState`.

#### Опциональные поля

`state.constraints`, `state.slots[].slotType`, `state.slots[].itemStackRef`, `state.slots[].embeddedStack`.

#### Миграции

- Slot count changes require explicit mapping: preserve, expand with empty slots, or move overflow to recovery inventory.
- Embedded stack and referenced stack models must not be mixed without migration strategy.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:inventory/container",
  "entityId": "inventory-player-4f1c9d4a-main",
  "persistenceScope": "PLAYER_PROFILE",
  "state": {
    "inventoryId": "inventory-player-4f1c9d4a-main",
    "owner": {
      "ownerType": "PLAYER",
      "ownerId": "player-4f1c9d4a",
      "ownershipRole": "OWNER"
    },
    "inventoryType": "player-main",
    "lockState": "UNLOCKED",
    "slots": [
      { "slotIndex": 0, "slotType": "general", "itemStackRef": "stack-01JSCRAP" }
    ]
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.7 Item Stack

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:item/stack` |
| Persistence Scope | Usually parent aggregate scope |
| Version | `1` |
| Primary Index | `entityType + stackId` for tracked stacks |
| Secondary Indexes | `state.itemId`, `state.locationRef`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/item/stack.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:item/stack" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["stackId", "itemId", "quantity"],
          "properties": {
            "stackId": { "type": "string" },
            "itemId": { "type": "string" },
            "quantity": { "$ref": "common/defs.schema.json#/$defs/itemQuantity" },
            "durability": { "$ref": "common/defs.schema.json#/$defs/durabilityState" },
            "metadata": { "type": "object" },
            "originRef": { "type": "string" },
            "locationRef": { "type": "string" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.stackId`, `state.itemId`, `state.quantity`.

#### Опциональные поля

`state.durability`, `state.metadata`, `state.originRef`, `state.locationRef`.

#### Миграции

- `itemId` rename requires item definition alias table and stack migration.
- Quantity policy migration must handle over-stacked documents through split or recovery rules.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:item/stack",
  "entityId": "stack-01JSCRAP",
  "persistenceScope": "PLAYER_PROFILE",
  "state": {
    "stackId": "stack-01JSCRAP",
    "itemId": "rustcraft:item/scrap",
    "quantity": 125,
    "locationRef": "inventory-player-4f1c9d4a-main:slot/0"
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.8 Equipment

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:equipment/loadout` |
| Persistence Scope | `PLAYER_PROFILE` for players, `WIPE_WORLD` or `TRANSIENT_SESSION` for NPCs |
| Version | `1` |
| Primary Index | `entityType + equipmentId` |
| Secondary Indexes | `state.actorRef`, `state.slots.itemStackRef`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/equipment/loadout.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:equipment/loadout" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["equipmentId", "actorRef", "slots"],
          "properties": {
            "equipmentId": { "type": "string" },
            "actorRef": { "type": "string" },
            "slots": {
              "type": "object",
              "additionalProperties": {
                "type": "object",
                "properties": {
                  "itemStackRef": { "type": "string" },
                  "constraints": { "type": "object" }
                }
              }
            },
            "derivedTags": { "type": "array", "items": { "type": "string" } }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.equipmentId`, `state.actorRef`, `state.slots`.

#### Опциональные поля

`state.slots.*.itemStackRef`, `state.slots.*.constraints`, `state.derivedTags`.

#### Миграции

- New equipment slot names require default empty slots and optional slot alias mapping.
- Removed slots must move item stacks to recovery inventory or mark migration failure.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:equipment/loadout",
  "entityId": "equipment-player-4f1c9d4a",
  "persistenceScope": "PLAYER_PROFILE",
  "state": {
    "equipmentId": "equipment-player-4f1c9d4a",
    "actorRef": "player-4f1c9d4a",
    "slots": {
      "head": {},
      "chest": {},
      "legs": {},
      "feet": {}
    }
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.9 Loot Container

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:loot/container` |
| Persistence Scope | `WIPE_WORLD`, `REGION`, or `TRANSIENT_SESSION` |
| Version | `1` |
| Primary Index | `entityType + lootContainerId` |
| Secondary Indexes | `state.lootTableId`, `state.region.regionId`, `state.lifecycle`, `state.respawn.nextAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/loot/container.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:loot/container" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["lootContainerId", "lifecycle", "sourceType", "lootTableId", "region"],
          "properties": {
            "lootContainerId": { "type": "string" },
            "lifecycle": { "enum": ["SPAWNED", "AVAILABLE", "OPENED", "LOOTED", "RESPAWNING", "DESPAWNED"] },
            "sourceType": { "type": "string" },
            "lootTableId": { "type": "string" },
            "inventoryRef": { "type": "string" },
            "region": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "respawn": { "type": "object" },
            "claim": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.lootContainerId`, `state.lifecycle`, `state.sourceType`, `state.lootTableId`, `state.region`.

#### Опциональные поля

`state.inventoryRef`, `state.respawn`, `state.claim`.

#### Миграции

- Source type changes require fallback source category.
- Missing `inventoryRef` can be regenerated only if container policy permits rerolling contents.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:loot/container",
  "entityId": "loot-01JROADCRATE",
  "persistenceScope": "REGION",
  "state": {
    "lootContainerId": "loot-01JROADCRATE",
    "lifecycle": "AVAILABLE",
    "sourceType": "road-crate",
    "lootTableId": "rustcraft:loot/road_crate_basic",
    "inventoryRef": "inventory-loot-01JROADCRATE",
    "region": { "worldId": "world-main", "regionId": "r.0.0" },
    "respawn": { "nextAt": "2026-06-02T01:00:00Z" }
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.10 Loot Table

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:loot/table` |
| Persistence Scope | `SERVER_GLOBAL` for active data snapshot or config/data pack storage |
| Version | `1` |
| Primary Index | `entityType + lootTableId` |
| Secondary Indexes | `state.progressionTier`, `state.tags`, `state.lifecycle` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/loot/table.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:loot/table" },
        "persistenceScope": { "const": "SERVER_GLOBAL" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["lootTableId", "lifecycle", "entries", "rollPolicy"],
          "properties": {
            "lootTableId": { "type": "string" },
            "lifecycle": { "enum": ["DECLARED", "VALIDATED", "ACTIVE", "DEPRECATED", "REPLACED"] },
            "progressionTier": { "type": "string" },
            "entries": { "type": "array", "items": { "type": "object" } },
            "rollPolicy": { "type": "object" },
            "conditions": { "type": "array", "items": { "type": "object" } },
            "tags": { "type": "array", "items": { "type": "string" } }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.lootTableId`, `state.lifecycle`, `state.entries`, `state.rollPolicy`.

#### Опциональные поля

`state.progressionTier`, `state.conditions`, `state.tags`.

#### Миграции

- Entry reward descriptors require backward-compatible aliases if renamed.
- Weight model changes must include deterministic migration notes for simulations and audit.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:loot/table",
  "entityId": "rustcraft:loot/road_crate_basic",
  "persistenceScope": "SERVER_GLOBAL",
  "state": {
    "lootTableId": "rustcraft:loot/road_crate_basic",
    "lifecycle": "ACTIVE",
    "progressionTier": "tier1",
    "entries": [
      { "rewardDescriptor": { "itemId": "rustcraft:item/scrap" }, "weight": 100, "quantity": { "min": 5, "max": 25 } }
    ],
    "rollPolicy": { "rolls": 3 }
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.11 Vehicle

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:vehicle/instance` |
| Persistence Scope | `WIPE_WORLD` |
| Version | `1` |
| Primary Index | `entityType + vehicleId` |
| Secondary Indexes | `state.owner.ownerType + ownerId`, `state.region.regionId`, `state.lifecycle`, `state.vehicleType` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/vehicle/instance.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:vehicle/instance" },
        "persistenceScope": { "const": "WIPE_WORLD" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["vehicleId", "vehicleType", "lifecycle", "owner", "region"],
          "properties": {
            "vehicleId": { "type": "string" },
            "vehicleType": { "type": "string" },
            "lifecycle": { "enum": ["SPAWNED", "OWNED", "AVAILABLE", "ACTIVE", "DAMAGED", "DECAYING", "DESTROYED", "DESPAWNED"] },
            "owner": { "$ref": "common/defs.schema.json#/$defs/ownerRef" },
            "region": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "fuel": { "type": "object" },
            "durability": { "$ref": "common/defs.schema.json#/$defs/durabilityState" },
            "inventoryRefs": { "type": "array", "items": { "type": "string" } },
            "accessPolicy": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.vehicleId`, `state.vehicleType`, `state.lifecycle`, `state.owner`, `state.region`.

#### Опциональные поля

`state.fuel`, `state.durability`, `state.inventoryRefs`, `state.accessPolicy`.

#### Миграции

- Vehicle type rename requires alias table and fallback category.
- Decay/despawn policy changes are config migrations, not schema migrations, unless persisted fields change.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:vehicle/instance",
  "entityId": "vehicle-01JBOAT",
  "persistenceScope": "WIPE_WORLD",
  "state": {
    "vehicleId": "vehicle-01JBOAT",
    "vehicleType": "boat-small",
    "lifecycle": "ACTIVE",
    "owner": {
      "ownerType": "PLAYER",
      "ownerId": "player-4f1c9d4a",
      "ownershipRole": "OWNER"
    },
    "region": { "worldId": "world-main", "regionId": "r.1.0" },
    "inventoryRefs": ["inventory-vehicle-01JBOAT"]
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.12 Economy Account

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:economy/account` |
| Persistence Scope | `SERVER_GLOBAL`, `PLAYER_PROFILE`, or `CLAN_PROFILE` by economy policy |
| Version | `1` |
| Primary Index | `entityType + accountId` |
| Secondary Indexes | `state.owner.ownerType + ownerId`, `state.status`, `state.balances.currency`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/economy/account.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:economy/account" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["accountId", "owner", "status", "balances", "transactionVersion"],
          "properties": {
            "accountId": { "type": "string" },
            "owner": { "$ref": "common/defs.schema.json#/$defs/ownerRef" },
            "status": { "enum": ["CREATED", "ACTIVE", "LOCKED", "MIGRATING", "CLOSED", "ARCHIVED"] },
            "balances": {
              "type": "array",
              "items": {
                "type": "object",
                "required": ["currency", "amount"],
                "properties": {
                  "currency": { "type": "string" },
                  "amount": { "type": "string", "pattern": "^-?[0-9]+(\\.[0-9]+)?$" }
                }
              }
            },
            "transactionVersion": { "type": "integer", "minimum": 0 },
            "debtPolicy": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.accountId`, `state.owner`, `state.status`, `state.balances`, `state.transactionVersion`.

#### Опциональные поля

`state.debtPolicy`, audit refs in `metadata`.

#### Миграции

- Currency key migration requires ledger-aware conversion and audit note.
- Numeric amounts are strings to avoid floating-point loss; migration must not convert them to binary floating point.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:economy/account",
  "entityId": "acct-player-4f1c9d4a-scrap",
  "persistenceScope": "PLAYER_PROFILE",
  "state": {
    "accountId": "acct-player-4f1c9d4a-scrap",
    "owner": {
      "ownerType": "PLAYER",
      "ownerId": "player-4f1c9d4a",
      "ownershipRole": "OWNER"
    },
    "status": "ACTIVE",
    "balances": [
      { "currency": "rustcraft:currency/scrap", "amount": "250" }
    ],
    "transactionVersion": 7
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.13 Safe Zone

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:world/safe-zone` |
| Persistence Scope | `REGION` or `SERVER_GLOBAL` for config-backed zones |
| Version | `1` |
| Primary Index | `entityType + safeZoneId` |
| Secondary Indexes | `state.region.regionId`, `state.lifecycle`, `state.policyRef`, `metadata.updatedAt` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/world/safe-zone.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:world/safe-zone" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["safeZoneId", "lifecycle", "region", "policyRef"],
          "properties": {
            "safeZoneId": { "type": "string" },
            "lifecycle": { "enum": ["DECLARED", "ACTIVE", "SUSPENDED", "REMOVED"] },
            "region": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "policyRef": { "type": "string" },
            "capabilityOverrides": { "type": "object" },
            "notificationPolicy": { "type": "object" },
            "monumentRef": { "type": "string" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.safeZoneId`, `state.lifecycle`, `state.region`, `state.policyRef`.

#### Опциональные поля

`state.capabilityOverrides`, `state.notificationPolicy`, `state.monumentRef`.

#### Миграции

- Policy migrations must define secure fallback; invalid safe zone policy should fail closed, not open.
- Region format migrations must rebuild region indexes.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:world/safe-zone",
  "entityId": "safezone-outpost-01",
  "persistenceScope": "REGION",
  "state": {
    "safeZoneId": "safezone-outpost-01",
    "lifecycle": "ACTIVE",
    "region": { "worldId": "world-main", "regionId": "outpost-region" },
    "policyRef": "rustcraft:policy/safe-zone/default",
    "monumentRef": "monument-outpost-01"
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

### 7.14 Monument

| Категория | Значение |
| --- | --- |
| Entity Type | `rustcraft:world/monument` |
| Persistence Scope | `WIPE_WORLD` or `REGION` |
| Version | `1` |
| Primary Index | `entityType + monumentId` |
| Secondary Indexes | `state.monumentTypeId`, `state.region.regionId`, `state.lifecycle`, `state.lootContainerRefs` |

#### JSON Schema

```json
{
  "$id": "https://github.com/RustCraft-Team/rustcraft-server/schemas/world/monument.v1.schema.json",
  "allOf": [
    { "$ref": "common/entity-envelope.schema.json" },
    {
      "properties": {
        "entityType": { "const": "rustcraft:world/monument" },
        "state": {
          "type": "object",
          "additionalProperties": false,
          "required": ["monumentId", "monumentTypeId", "lifecycle", "region"],
          "properties": {
            "monumentId": { "type": "string" },
            "monumentTypeId": { "type": "string" },
            "lifecycle": { "enum": ["DECLARED", "PLACED", "ACTIVE", "DISABLED", "REMOVED_BY_WIPE"] },
            "region": { "$ref": "common/defs.schema.json#/$defs/regionRef" },
            "lootContainerRefs": { "type": "array", "items": { "type": "string" } },
            "npcSpawnRefs": { "type": "array", "items": { "type": "string" } },
            "safeZoneRefs": { "type": "array", "items": { "type": "string" } },
            "resetPolicy": { "type": "object" }
          }
        }
      }
    }
  ]
}
```

#### Обязательные поля

`state.monumentId`, `state.monumentTypeId`, `state.lifecycle`, `state.region`.

#### Опциональные поля

`state.lootContainerRefs`, `state.npcSpawnRefs`, `state.safeZoneRefs`, `state.resetPolicy`.

#### Миграции

- Monument type migrations must not imply generation or placement logic in data contracts.
- Missing loot/NPC refs can be rebuilt only from deterministic source definitions and with audit marker.

#### Пример JSON

```json
{
  "schemaVersion": 1,
  "entityType": "rustcraft:world/monument",
  "entityId": "monument-outpost-01",
  "persistenceScope": "REGION",
  "state": {
    "monumentId": "monument-outpost-01",
    "monumentTypeId": "rustcraft:monument/outpost",
    "lifecycle": "ACTIVE",
    "region": { "worldId": "world-main", "regionId": "outpost-region" },
    "lootContainerRefs": ["loot-01JROADCRATE"],
    "safeZoneRefs": ["safezone-outpost-01"]
  },
  "metadata": {
    "createdAt": "2026-06-02T00:00:00Z",
    "updatedAt": "2026-06-02T00:00:00Z",
    "sourceModule": "rustcraft-api"
  }
}
```

## 8. Cross-entity migration rules

### 8.1 Identity preservation

- Migrations must preserve `entityId` unless a dedicated re-key migration is approved by ADR.
- Re-key migrations must update all `refs` and indexes in one transactional boundary.
- Display names, coordinates and tags are never sufficient as identity.

### 8.2 Reference integrity

- `refs` must point to stable aggregate ids.
- Missing optional refs are tolerated if the entity defines fallback behavior.
- Missing required refs must fail validation or trigger recovery migration.
- Cyclic refs are allowed only through ids, not embedded object graphs.

### 8.3 Scope changes

- Moving an entity between scopes requires explicit migration note.
- `WIPE_WORLD -> SERVER_GLOBAL` is allowed only for archived summaries, not live world objects.
- `TRANSIENT_SESSION` documents must not be promoted to persisted scopes without explicit schema.

### 8.4 Wipe behavior

| Scope | Default wipe behavior |
| --- | --- |
| `SERVER_GLOBAL` | Keep unless config says reset. |
| `PLAYER_PROFILE` | Keep profile; wipe-linked refs removed or archived. |
| `CLAN_PROFILE` | Keep clan; wipe-linked refs removed or archived. |
| `WIPE_WORLD` | Remove on wipe unless archived summary policy exists. |
| `REGION` | Remove or rebuild depending on world generation policy. |
| `TRANSIENT_SESSION` | Never persisted across restart/wipe. |

## 9. Backward compatibility rules

1. A reader for version `N` should reject version `N+1` unless the schema declares forward-compatible optional-only changes.
2. A reader for version `N+1` must support reading version `N` through migration or compatibility adapter.
3. Unknown fields are rejected by default because schemas use `additionalProperties: false`.
4. Unknown enum values require documented fallback before they can be introduced in a minor schema update.
5. Required field additions require backfill rule, default derivation, or major migration.
6. Field removals are performed in two steps: deprecate in version `N`, remove in version `N+1` only after migration.
7. Numeric economy fields must remain string-encoded decimals unless a dedicated precision ADR changes this rule.
8. Runtime-only objects, Fabric handles, callbacks and service references must never be serialized.
9. JSON document examples are illustrative but must remain valid against the documented architectural schema intent.
10. Every data contract change must update this document or a follow-up ADR before implementation.

## 10. Validation checklist for future implementation

Before implementing code for any data contract, verify:

- entity has stable `entityType` and `entityId` rules;
- JSON Schema exists and uses the common envelope;
- required and optional fields are documented;
- persistence scope and wipe behavior are explicit;
- indexes are defined;
- migration path from prior version exists;
- examples are updated;
- no Minecraft registry entry, block, item, recipe, NPC or monument implementation is introduced by the data contract.
