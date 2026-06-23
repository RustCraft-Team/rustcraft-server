package team.rustcraft.api.building;

import java.util.Collection;
import team.rustcraft.api.player.PlayerId;

/** Minecraft-independent aggregate of connected RustCraft building blocks. */
public interface Building {
    BuildingId id();

    PlayerId owner();

    Collection<BuildingBlock> blocks();
}
