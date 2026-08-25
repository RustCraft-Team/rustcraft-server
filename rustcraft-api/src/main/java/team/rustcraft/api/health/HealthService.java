package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/** Domain-only service for player health, damage, bleeding, health-owned hunger and regeneration. */
public interface HealthService {
    HealthProfile createProfile(PlayerId playerId, double maxHealth, double hunger, Instant now);
    Optional<HealthProfile> findProfile(PlayerId playerId);
    HealthProfile damage(PlayerId playerId, double amount, DamageSource source, Instant now);
    HealthProfile heal(PlayerId playerId, double amount, Instant now);
    HealthProfile startBleeding(PlayerId playerId, int stacks, Instant now);
    HealthProfile stopBleeding(PlayerId playerId, Instant now);
    HealthProfile setHunger(PlayerId playerId, double hunger, Instant now);
    HealthProfile tick(PlayerId playerId, Instant now);
}
