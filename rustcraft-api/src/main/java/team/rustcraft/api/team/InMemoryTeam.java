package team.rustcraft.api.team;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import team.rustcraft.api.player.PlayerId;

/**
 * Immutable in-memory team implementation intended for tests and lightweight servers.
 */
public record InMemoryTeam(
        TeamId id,
        String name,
        PlayerId leaderId,
        Set<PlayerId> members,
        Map<String, String> tags,
        boolean friendlyFireDisabled,
        int maxSize) implements Team {
    public InMemoryTeam {
        Objects.requireNonNull(id, "id");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name must not be blank");
        }
        Objects.requireNonNull(leaderId, "leaderId");
        if (maxSize < 1) {
            throw new IllegalArgumentException("Max team size must be at least 1");
        }
        members = Set.copyOf(new LinkedHashSet<>(Objects.requireNonNull(members, "members")));
        if (!members.contains(leaderId)) {
            throw new IllegalArgumentException("Team members must include the leader");
        }
        if (members.size() > maxSize) {
            throw new IllegalArgumentException("Team size exceeds max team size");
        }
        tags = Map.copyOf(new LinkedHashMap<>(Objects.requireNonNull(tags, "tags")));
    }
}
