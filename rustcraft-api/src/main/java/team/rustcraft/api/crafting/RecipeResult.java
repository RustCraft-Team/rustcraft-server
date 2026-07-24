package team.rustcraft.api.crafting;

import team.rustcraft.api.item.ItemDefinitionId;

/** Item and amount produced by a RustCraft crafting recipe. */
public record RecipeResult(ItemDefinitionId itemId, int amount) {
    public RecipeResult {
        if (itemId == null) {
            throw new IllegalArgumentException("Result item id must not be null");
        }
        if (amount < 1) {
            throw new IllegalArgumentException("Result amount must be at least 1");
        }
    }
}
