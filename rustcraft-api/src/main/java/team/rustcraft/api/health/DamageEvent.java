package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched after health damage is applied. */
public record DamageEvent(PlayerId playerId, double amount, DamageSource source, HealthProfile profile, Instant occurredAt) implements Event {
    public DamageEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
