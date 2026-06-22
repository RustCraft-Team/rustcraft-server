package team.rustcraft.api.death;

/**
 * Minecraft-independent world position used by RustCraft domain models.
 *
 * @param worldId stable world or dimension identifier
 * @param x block x coordinate
 * @param y block y coordinate
 * @param z block z coordinate
 */
public record WorldPosition(String worldId, int x, int y, int z) {
    /**
     * Creates a world position after validating the world id.
     */
    public WorldPosition {
        if (worldId == null || worldId.isBlank()) {
            throw new IllegalArgumentException("World id must not be blank");
        }
    }
}
