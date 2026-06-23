package team.rustcraft.api.building;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/** In-memory {@link Building}. */
public final class InMemoryBuilding implements Building {
    private final BuildingId id;
    private final PlayerId owner;
    private final Map<BuildingBlockId, BuildingBlock> blocks = new LinkedHashMap<>();

    public InMemoryBuilding(BuildingId id, PlayerId owner) {
        this.id = Objects.requireNonNull(id, "id");
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    @Override public BuildingId id() { return id; }

    @Override public PlayerId owner() { return owner; }

    @Override public Collection<BuildingBlock> blocks() { return List.copyOf(blocks.values()); }

    void addBlock(BuildingBlock block) { blocks.put(block.id(), block); }

    BuildingBlock removeBlock(BuildingBlockId id) { return blocks.remove(id); }
}
