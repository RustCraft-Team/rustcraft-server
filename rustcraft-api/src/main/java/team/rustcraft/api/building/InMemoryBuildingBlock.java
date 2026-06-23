package team.rustcraft.api.building;

import java.util.Objects;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;

/** In-memory {@link BuildingBlock}. */
public final class InMemoryBuildingBlock implements BuildingBlock {
    private final BuildingBlockId id;
    private final BuildingId buildingId;
    private final BuildingBlockType type;
    private final BuildingGrade grade;
    private final int maxHealth;
    private final int currentHealth;
    private final PlayerId owner;
    private final WorldPosition position;

    public InMemoryBuildingBlock(BuildingBlockId id, BuildingId buildingId, BuildingBlockType type, BuildingGrade grade, int maxHealth, int currentHealth, PlayerId owner, WorldPosition position) {
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        if (currentHealth < 0 || currentHealth > maxHealth) {
            throw new IllegalArgumentException("Current health must be between zero and max health");
        }
        this.id = Objects.requireNonNull(id, "id");
        this.buildingId = Objects.requireNonNull(buildingId, "buildingId");
        this.type = Objects.requireNonNull(type, "type");
        this.grade = Objects.requireNonNull(grade, "grade");
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.owner = Objects.requireNonNull(owner, "owner");
        this.position = Objects.requireNonNull(position, "position");
    }

    @Override public BuildingBlockId id() { return id; }
    @Override public BuildingId buildingId() { return buildingId; }
    @Override public BuildingBlockType type() { return type; }
    @Override public BuildingGrade grade() { return grade; }
    @Override public int maxHealth() { return maxHealth; }
    @Override public int currentHealth() { return currentHealth; }
    @Override public PlayerId owner() { return owner; }
    @Override public WorldPosition position() { return position; }
}
