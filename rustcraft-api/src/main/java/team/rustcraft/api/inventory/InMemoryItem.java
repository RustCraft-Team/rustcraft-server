package team.rustcraft.api.inventory;

import java.util.Objects;

/**
 * Immutable in-memory item definition.
 */
public record InMemoryItem(ItemId id, String displayName, int maxStackSize) implements Item {
    public InMemoryItem {
        Objects.requireNonNull(id, "id");
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Display name must not be blank");
        }
        if (maxStackSize < 1) {
            throw new IllegalArgumentException("Max stack size must be at least 1");
        }
    }
}
