package team.rustcraft.api.death;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;

/**
 * Simple in-memory {@link RespawnService} for tests and local-only usage.
 */
public final class InMemoryRespawnService implements RespawnService {
    public static final WorldPosition DEFAULT_BEACH_POSITION = new WorldPosition("minecraft:overworld", 0, 64, 0);
    public static final MapColor DEFAULT_BEACH_MAP_COLOR = new MapColor(240, 220, 130);

    private final Map<RespawnPointId, RespawnPoint> respawnPoints = new LinkedHashMap<>();
    private final Map<CorpseId, Corpse> corpses = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryRespawnService(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized RespawnPoint createRespawnPoint(RespawnPointId id, PlayerId owner, WorldPosition position, RespawnPointType type, MapColor mapColor, boolean active) {
        Objects.requireNonNull(id, "id");
        if (respawnPoints.containsKey(id)) {
            throw new IllegalArgumentException("Respawn point already exists: " + id.value());
        }
        RespawnPoint respawnPoint = new InMemoryRespawnPoint(id, owner, position, type, mapColor, active);
        respawnPoints.put(id, respawnPoint);
        eventBus.dispatch(new RespawnPointCreatedEvent(respawnPoint));
        return respawnPoint;
    }

    @Override
    public synchronized boolean destroyRespawnPoint(RespawnPointId id) {
        Objects.requireNonNull(id, "id");
        RespawnPoint removed = respawnPoints.remove(id);
        if (removed == null) {
            return false;
        }
        eventBus.dispatch(new RespawnPointDestroyedEvent(removed));
        return true;
    }

    @Override
    public synchronized Optional<RespawnPoint> findRespawnPoint(RespawnPointId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(respawnPoints.get(id));
    }

    @Override
    public synchronized Collection<RespawnPoint> respawnPoints(PlayerId owner) {
        Objects.requireNonNull(owner, "owner");
        return respawnPoints.values().stream().filter(point -> point.owner().equals(owner)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public RespawnPoint beachRespawnPoint(PlayerId owner) {
        Objects.requireNonNull(owner, "owner");
        return new InMemoryRespawnPoint(beachId(owner), owner, DEFAULT_BEACH_POSITION, RespawnPointType.BEACH, DEFAULT_BEACH_MAP_COLOR, true);
    }

    @Override
    public synchronized RespawnPoint respawn(PlayerId playerId, RespawnPointId respawnPointId) {
        Objects.requireNonNull(playerId, "playerId");
        RespawnPoint respawnPoint = resolveRespawnPoint(playerId, respawnPointId);
        if (!respawnPoint.owner().equals(playerId)) {
            throw new IllegalArgumentException("Respawn point belongs to another player");
        }
        if (!respawnPoint.active()) {
            throw new IllegalStateException("Respawn point is not active");
        }
        eventBus.dispatch(new PlayerRespawnedEvent(playerId, respawnPoint));
        return respawnPoint;
    }

    @Override
    public synchronized Corpse createCorpse(CorpseId id, PlayerId owner, WorldPosition position, Instant createdAt) {
        Objects.requireNonNull(id, "id");
        if (corpses.containsKey(id)) {
            throw new IllegalArgumentException("Corpse already exists: " + id.value());
        }
        Corpse corpse = new InMemoryCorpse(id, owner, position, createdAt);
        corpses.put(id, corpse);
        eventBus.dispatch(new PlayerDiedEvent(corpse));
        return corpse;
    }

    @Override
    public synchronized Optional<Corpse> findCorpse(CorpseId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(corpses.get(id));
    }

    @Override
    public synchronized Collection<Corpse> corpses(PlayerId owner) {
        Objects.requireNonNull(owner, "owner");
        return corpses.values().stream().filter(corpse -> corpse.owner().equals(owner)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private RespawnPoint resolveRespawnPoint(PlayerId playerId, RespawnPointId respawnPointId) {
        Objects.requireNonNull(respawnPointId, "respawnPointId");
        if (respawnPointId.equals(beachId(playerId))) {
            return beachRespawnPoint(playerId);
        }
        RespawnPoint respawnPoint = respawnPoints.get(respawnPointId);
        if (respawnPoint == null) {
            throw new IllegalArgumentException("Unknown respawn point: " + respawnPointId.value());
        }
        return respawnPoint;
    }

    private static RespawnPointId beachId(PlayerId owner) {
        return new RespawnPointId("beach:" + owner.value());
    }
}
