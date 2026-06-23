package team.rustcraft.api.inventory;

import java.util.Objects;

/**
 * Immutable in-memory item stack snapshot.
 */
public record InMemoryItemStack(ItemStackId id, ItemId itemId, int amount) implements ItemStack {
    public InMemoryItemStack {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(itemId, "itemId");
        if (amount < 1) {
            throw new IllegalArgumentException("Stack amount must be at least 1");
        }
    }
}
