package team.rustcraft.api.team;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event published after a player joins a team. */
public record TeamMemberJoinedEvent(Team team, PlayerId playerId, TeamRole role, Instant occurredAt) implements Event {
    public TeamMemberJoinedEvent {
        Objects.requireNonNull(team, "team");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TeamMemberJoinedEvent(Team team, PlayerId playerId, TeamRole role) {
        this(team, playerId, role, Instant.now());
    }
}
