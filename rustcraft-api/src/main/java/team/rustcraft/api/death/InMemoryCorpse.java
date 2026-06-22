package team.rustcraft.api.death;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/**
 * Immutable in-memory corpse implementation.
 */
public record InMemoryCorpse(CorpseId id, PlayerId owner, WorldPosition position, Instant createdAt) implements Corpse {
    public InMemoryCorpse {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(createdAt, "createdAt");
    }
}
