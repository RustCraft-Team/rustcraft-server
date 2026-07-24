package team.rustcraft.api.crafting;

import team.rustcraft.api.item.ItemDefinitionId;

/** Item and amount consumed by a RustCraft crafting recipe. */
public record RecipeIngredient(ItemDefinitionId itemId, int amount) {
    public RecipeIngredient {
        if (itemId == null) {
            throw new IllegalArgumentException("Ingredient item id must not be null");
        }
        if (amount < 1) {
            throw new IllegalArgumentException("Ingredient amount must be at least 1");
        }
    }
}
