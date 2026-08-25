package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event published when the survival hunger value changes through FoodService. */
public record HungerChangedEvent(PlayerId playerId, double previousHunger, double newHunger, FoodProfile profile, Instant occurredAt) implements Event {
    public HungerChangedEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
