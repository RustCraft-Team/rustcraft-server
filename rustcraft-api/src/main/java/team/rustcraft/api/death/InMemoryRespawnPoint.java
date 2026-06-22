package team.rustcraft.api.death;

import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/**
 * Immutable in-memory respawn point implementation.
 */
public record InMemoryRespawnPoint(
        RespawnPointId id,
        PlayerId owner,
        WorldPosition position,
        RespawnPointType type,
        MapColor mapColor,
        boolean active) implements RespawnPoint {
    public InMemoryRespawnPoint {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(mapColor, "mapColor");
    }
}
