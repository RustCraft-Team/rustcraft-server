package team.rustcraft.api.crafting;

import java.util.List;

/** Domain-only item repair cost definition associated with a crafting recipe. */
public record RepairCostDefinition(List<RecipeIngredient> ingredients, int scrapCost) {
    public static final RepairCostDefinition NONE = new RepairCostDefinition(List.of(), 0);

    public RepairCostDefinition {
        if (scrapCost < 0) {
            throw new IllegalArgumentException("Repair scrap cost must not be negative");
        }
        ingredients = List.copyOf(ingredients == null ? List.of() : ingredients);
    }

    public boolean required() {
        return scrapCost > 0 || !ingredients.isEmpty();
    }
}
