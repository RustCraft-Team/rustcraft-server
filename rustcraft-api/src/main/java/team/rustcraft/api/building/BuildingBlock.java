package team.rustcraft.api.building;

import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;

/** Minecraft-independent RustCraft building block domain object. */
public interface BuildingBlock {
    BuildingBlockId id();

    BuildingId buildingId();

    BuildingBlockType type();

    BuildingGrade grade();

    int maxHealth();

    int currentHealth();

    PlayerId owner();

    WorldPosition position();
}
