package team.rustcraft.api.blueprint;

/**
 * Stable RustCraft identifier for a player-owned blueprint entry.
 *
 * @param value serialized blueprint id
 */
public record BlueprintId(String value) {
    public BlueprintId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Blueprint id must not be blank");
        }
    }
}
