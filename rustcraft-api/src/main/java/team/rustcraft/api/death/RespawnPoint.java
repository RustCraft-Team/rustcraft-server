package team.rustcraft.api.death;

import team.rustcraft.api.player.PlayerId;

/**
 * Minecraft-independent respawn point owned by a single player.
 */
public interface RespawnPoint {
    RespawnPointId id();

    PlayerId owner();

    WorldPosition position();

    RespawnPointType type();

    MapColor mapColor();

    boolean active();
}
