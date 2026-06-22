package team.rustcraft.api.team;

import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.player.PlayerProfile;

/**
 * Simple immutable player profile for tests.
 */
public record InMemoryPlayerProfile(PlayerId id, String name, Optional<TeamId> teamId) implements PlayerProfile {
    public InMemoryPlayerProfile {
        Objects.requireNonNull(id, "id");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank");
        }
        teamId = Objects.requireNonNull(teamId, "teamId");
    }

    public InMemoryPlayerProfile(PlayerId id, String name) {
        this(id, name, Optional.empty());
    }
}
