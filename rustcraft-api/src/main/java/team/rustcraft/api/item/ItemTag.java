package team.rustcraft.api.item;

/** Searchable domain tag assigned to item definitions. */
public record ItemTag(String value) {
    public ItemTag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Item tag must not be blank");
        }
    }
}
