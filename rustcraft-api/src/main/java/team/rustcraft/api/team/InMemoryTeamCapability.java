package team.rustcraft.api.team;

import java.util.Objects;

/**
 * Simple team capability implementation backed by an immutable team snapshot.
 */
public record InMemoryTeamCapability(Team team) implements TeamCapability {
    public InMemoryTeamCapability {
        Objects.requireNonNull(team, "team");
    }
}
