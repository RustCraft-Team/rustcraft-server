package team.rustcraft.api.team;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;

/** Event published after a team is disbanded. */
public record TeamDisbandedEvent(Team team, Instant occurredAt) implements Event {
    public TeamDisbandedEvent {
        Objects.requireNonNull(team, "team");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TeamDisbandedEvent(Team team) {
        this(team, Instant.now());
    }
}
