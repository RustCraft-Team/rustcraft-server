package team.rustcraft.api.item;

/** Arbitrary domain metadata attached to an item definition. */
public record ItemProperty(String name, String value) {
    public ItemProperty {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item property name must not be blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Item property value must not be null");
        }
    }
}
