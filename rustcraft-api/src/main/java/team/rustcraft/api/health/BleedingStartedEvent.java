package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched when bleeding stacks are added. */
public record BleedingStartedEvent(PlayerId playerId, int stacksAdded, int totalStacks, Instant occurredAt) implements Event {
    public BleedingStartedEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
