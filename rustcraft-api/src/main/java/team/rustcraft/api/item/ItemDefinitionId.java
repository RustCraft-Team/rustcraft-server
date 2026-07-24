package team.rustcraft.api.item;

/** Stable RustCraft item definition identifier. */
public record ItemDefinitionId(String value) {
    public ItemDefinitionId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item definition id must not be blank");
        }
    }
}
