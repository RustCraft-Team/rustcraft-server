package team.rustcraft.api.death;

/**
 * Stable identifier for a RustCraft respawn point.
 *
 * @param value non-blank respawn point identifier
 */
public record RespawnPointId(String value) {
    /**
     * Creates a respawn point id after validating its value.
     */
    public RespawnPointId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Respawn point id must not be blank");
        }
    }
}
