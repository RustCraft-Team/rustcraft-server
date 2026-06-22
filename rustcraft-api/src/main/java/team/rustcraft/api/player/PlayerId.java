package team.rustcraft.api.player;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable RustCraft identifier for a Minecraft player.
 *
 * @param value player UUID
 */
public record PlayerId(UUID value) {
    /**
     * Creates a player id after validating the UUID.
     */
    public PlayerId {
        Objects.requireNonNull(value, "value");
    }

    /**
     * Creates a player id from a UUID string.
     *
     * @param value UUID string
     * @return player id
     */
    public static PlayerId parse(String value) {
        return new PlayerId(UUID.fromString(value));
    }
}
