package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched once when a living player reaches zero health. */
public record PlayerKilledEvent(PlayerId playerId, DamageSource source, Optional<PlayerId> killer, Instant occurredAt) implements Event {
    public PlayerKilledEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(source, "source");
        killer = Objects.requireNonNull(killer, "killer");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
