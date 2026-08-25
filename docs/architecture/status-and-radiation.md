# Status, hunger, and radiation ownership

RustCraft API keeps survival state domain-first and independent from Minecraft/Fabric runtime classes.

- `HealthProfile` owns mutable hunger because hunger directly gates health regeneration. `FoodProfile` is a read model composed from `HealthService` plus food-only active effects, so food consumption updates hunger through `HealthService` and does not keep a second mutable hunger value.
- `RadiationProfile` owns mutable radiation exposure. `HealthProfile` does not store radiation; `RadiationService` applies radiation damage by calling `HealthService.damage` with the domain `DamageType.RADIATION` source.
- Generic status effects use RustCraft identifiers and callbacks only. They intentionally do not register or model Minecraft status effects.
