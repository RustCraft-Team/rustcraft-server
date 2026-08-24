package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched after health is restored. */
public record HealEvent(PlayerId playerId, double amount, HealthProfile profile, Instant occurredAt) implements Event {
    public HealEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
