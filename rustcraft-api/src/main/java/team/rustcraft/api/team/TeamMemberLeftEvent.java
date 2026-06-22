package team.rustcraft.api.team;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event published after a player leaves a team. */
public record TeamMemberLeftEvent(Team team, PlayerId playerId, TeamRole previousRole, Instant occurredAt) implements Event {
    public TeamMemberLeftEvent {
        Objects.requireNonNull(team, "team");
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(previousRole, "previousRole");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TeamMemberLeftEvent(Team team, PlayerId playerId, TeamRole previousRole) {
        this(team, playerId, previousRole, Instant.now());
    }
}
