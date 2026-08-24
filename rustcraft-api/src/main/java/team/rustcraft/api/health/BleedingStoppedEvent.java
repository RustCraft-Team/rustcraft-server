package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched when all bleeding stacks are removed. */
public record BleedingStoppedEvent(PlayerId playerId, int stacksRemoved, Instant occurredAt) implements Event {
    public BleedingStoppedEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
