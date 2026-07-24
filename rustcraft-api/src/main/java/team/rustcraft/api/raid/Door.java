package team.rustcraft.api.raid;

import java.util.Optional;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.player.PlayerId;

/** Minecraft-independent RustCraft door domain object. */
public interface Door {
    DoorId id();
    PlayerId owner();
    BuildingId buildingId();
    DoorType type();
    int currentHealth();
    int maxHealth();
    boolean isOpen();
    Optional<LockId> lockId();
}
