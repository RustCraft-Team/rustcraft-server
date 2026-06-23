package team.rustcraft.api.inventory;

/**
 * Stable RustCraft item identifier.
 *
 * @param value non-blank item identifier
 */
public record ItemId(String value) {
    public ItemId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item id must not be blank");
        }
    }
}
