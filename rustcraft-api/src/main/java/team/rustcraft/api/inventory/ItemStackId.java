package team.rustcraft.api.inventory;

/**
 * Stable RustCraft item stack identifier.
 *
 * @param value non-blank item stack identifier
 */
public record ItemStackId(String value) {
    public ItemStackId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item stack id must not be blank");
        }
    }
}
