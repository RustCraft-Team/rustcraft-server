package team.rustcraft.api.death;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/**
 * Domain service for respawn points and corpses.
 */
public interface RespawnService {
    RespawnPoint createRespawnPoint(RespawnPointId id, PlayerId owner, WorldPosition position, RespawnPointType type, MapColor mapColor, boolean active);

    boolean destroyRespawnPoint(RespawnPointId id);

    Optional<RespawnPoint> findRespawnPoint(RespawnPointId id);

    Collection<RespawnPoint> respawnPoints(PlayerId owner);

    RespawnPoint beachRespawnPoint(PlayerId owner);

    RespawnPoint respawn(PlayerId playerId, RespawnPointId respawnPointId);

    Corpse createCorpse(CorpseId id, PlayerId owner, WorldPosition position, Instant createdAt);

    Optional<Corpse> findCorpse(CorpseId id);

    Collection<Corpse> corpses(PlayerId owner);
}
