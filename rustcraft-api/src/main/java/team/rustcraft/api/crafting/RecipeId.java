package team.rustcraft.api.crafting;

/** Stable RustCraft crafting recipe identifier. */
public record RecipeId(String value) {
    public RecipeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Recipe id must not be blank");
        }
    }
}
