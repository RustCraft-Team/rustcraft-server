package team.rustcraft.api.inventory;

/**
 * Stable RustCraft inventory identifier.
 *
 * @param value non-blank inventory identifier
 */
public record InventoryId(String value) {
    public InventoryId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Inventory id must not be blank");
        }
    }
}
