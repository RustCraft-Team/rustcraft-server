package team.rustcraft.api.health;

import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/** Immutable {@link DamageSource} implementation for domain tests and integrations. */
public record SimpleDamageSource(DamageType type, Optional<PlayerId> attacker, String description) implements DamageSource {
    public SimpleDamageSource {
        Objects.requireNonNull(type, "type");
        attacker = Objects.requireNonNull(attacker, "attacker");
        description = Objects.requireNonNull(description, "description");
    }

    public SimpleDamageSource(DamageType type, String description) {
        this(type, Optional.empty(), description);
    }
}
