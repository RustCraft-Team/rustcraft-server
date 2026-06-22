package team.rustcraft.api.team;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;

/** Event published after a team is created. */
public record TeamCreatedEvent(Team team, Instant occurredAt) implements Event {
    public TeamCreatedEvent {
        Objects.requireNonNull(team, "team");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TeamCreatedEvent(Team team) {
        this(team, Instant.now());
    }
}
