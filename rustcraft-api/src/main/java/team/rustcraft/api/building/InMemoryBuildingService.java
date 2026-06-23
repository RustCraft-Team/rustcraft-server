package team.rustcraft.api.building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Simple in-memory {@link BuildingService} for tests and local-only usage. */
public final class InMemoryBuildingService implements BuildingService {
    private final Map<BuildingId, InMemoryBuilding> buildings = new LinkedHashMap<>();
    private final Map<BuildingBlockId, BuildingBlock> blocks = new LinkedHashMap<>();
    private final Map<ToolCupboardId, InMemoryToolCupboard> toolCupboards = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryBuildingService(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override public synchronized Building createBuilding(BuildingId id, PlayerId owner) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(owner, "owner");
        if (buildings.containsKey(id)) throw new IllegalArgumentException("Building already exists: " + id.value());
        InMemoryBuilding building = new InMemoryBuilding(id, owner);
        buildings.put(id, building);
        eventBus.dispatch(new BuildingCreatedEvent(building));
        return building;
    }

    @Override public synchronized Optional<Building> findBuilding(BuildingId id) { return Optional.ofNullable(buildings.get(Objects.requireNonNull(id, "id"))); }

    @Override public synchronized boolean destroyBuilding(BuildingId id) {
        InMemoryBuilding removed = buildings.remove(Objects.requireNonNull(id, "id"));
        if (removed == null) return false;
        for (BuildingBlock block : new ArrayList<>(removed.blocks())) blocks.remove(block.id());
        eventBus.dispatch(new BuildingDestroyedEvent(removed));
        return true;
    }

    @Override public synchronized BuildingBlock addBlock(BuildingBlockId id, BuildingId buildingId, BuildingBlockType type, BuildingGrade grade, int maxHealth, WorldPosition position) {
        Objects.requireNonNull(id, "id");
        if (blocks.containsKey(id)) throw new IllegalArgumentException("Building block already exists: " + id.value());
        InMemoryBuilding building = buildings.get(Objects.requireNonNull(buildingId, "buildingId"));
        if (building == null) throw new IllegalArgumentException("Unknown building: " + buildingId.value());
        BuildingBlock block = new InMemoryBuildingBlock(id, buildingId, type, grade, maxHealth, maxHealth, building.owner(), position);
        blocks.put(id, block);
        building.addBlock(block);
        return block;
    }

    @Override public synchronized boolean destroyBlock(BuildingBlockId id) {
        BuildingBlock removed = blocks.remove(Objects.requireNonNull(id, "id"));
        if (removed == null) return false;
        InMemoryBuilding building = buildings.get(removed.buildingId());
        if (building != null) building.removeBlock(id);
        return true;
    }

    @Override public synchronized Optional<BuildingBlock> findBlock(BuildingBlockId id) { return Optional.ofNullable(blocks.get(Objects.requireNonNull(id, "id"))); }

    @Override public synchronized ToolCupboard placeToolCupboard(ToolCupboardId id, PlayerId owner, WorldPosition position, boolean active) {
        Objects.requireNonNull(id, "id");
        if (toolCupboards.containsKey(id)) throw new IllegalArgumentException("Tool cupboard already exists: " + id.value());
        InMemoryToolCupboard toolCupboard = new InMemoryToolCupboard(id, owner, position, active);
        toolCupboards.put(id, toolCupboard);
        eventBus.dispatch(new ToolCupboardPlacedEvent(toolCupboard));
        return toolCupboard;
    }

    @Override public synchronized boolean destroyToolCupboard(ToolCupboardId id) {
        ToolCupboard removed = toolCupboards.remove(Objects.requireNonNull(id, "id"));
        if (removed == null) return false;
        eventBus.dispatch(new ToolCupboardDestroyedEvent(removed));
        return true;
    }

    @Override public synchronized Optional<ToolCupboard> findToolCupboard(ToolCupboardId id) { return Optional.ofNullable(toolCupboards.get(Objects.requireNonNull(id, "id"))); }
    @Override public synchronized Collection<ToolCupboard> toolCupboards() { return new ArrayList<>(toolCupboards.values()); }

    @Override public synchronized ToolCupboard authorizePlayer(ToolCupboardId id, PlayerId playerId) {
        InMemoryToolCupboard tc = requireToolCupboard(id);
        if (tc.authorizePlayer(playerId)) eventBus.dispatch(new PlayerAuthorizedEvent(tc, playerId));
        return tc;
    }

    @Override public synchronized ToolCupboard deauthorizePlayer(ToolCupboardId id, PlayerId playerId) {
        InMemoryToolCupboard tc = requireToolCupboard(id);
        if (tc.deauthorizePlayer(playerId)) eventBus.dispatch(new PlayerDeauthorizedEvent(tc, playerId));
        return tc;
    }

    @Override public synchronized ToolCupboard authorizeTeam(ToolCupboardId id, TeamId teamId) {
        InMemoryToolCupboard tc = requireToolCupboard(id);
        tc.authorizeTeam(teamId);
        return tc;
    }

    @Override public synchronized ToolCupboard deauthorizeTeam(ToolCupboardId id, TeamId teamId) {
        InMemoryToolCupboard tc = requireToolCupboard(id);
        tc.deauthorizeTeam(teamId);
        return tc;
    }

    private InMemoryToolCupboard requireToolCupboard(ToolCupboardId id) {
        InMemoryToolCupboard tc = toolCupboards.get(Objects.requireNonNull(id, "id"));
        if (tc == null) throw new IllegalArgumentException("Unknown tool cupboard: " + id.value());
        return tc;
    }
}
