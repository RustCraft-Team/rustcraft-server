package team.rustcraft.api.crafting;

import java.time.Duration;
import java.util.List;

/**
 * Pure RustCraft crafting recipe definition independent from Minecraft/Fabric systems.
 */
public record CraftingRecipe(
        RecipeId id,
        String displayName,
        RecipeCategory category,
        ResearchRequirement researchRequirement,
        WorkbenchTier workbenchTierRequirement,
        Duration craftingTime,
        List<RecipeIngredient> ingredients,
        List<RecipeResult> outputs,
        List<RecipeResult> byproducts,
        boolean blueprintRequired,
        boolean unlocked,
        RepairCostDefinition repairCostDefinition
) {
    public CraftingRecipe {
        if (id == null) throw new IllegalArgumentException("Recipe id must not be null");
        if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("Recipe display name must not be blank");
        if (category == null) throw new IllegalArgumentException("Recipe category must not be null");
        researchRequirement = researchRequirement == null ? ResearchRequirement.NONE : researchRequirement;
        if (workbenchTierRequirement == null) throw new IllegalArgumentException("Workbench tier requirement must not be null");
        if (craftingTime == null || craftingTime.isNegative()) throw new IllegalArgumentException("Crafting time must not be negative");
        ingredients = List.copyOf(ingredients == null ? List.of() : ingredients);
        outputs = List.copyOf(outputs == null ? List.of() : outputs);
        byproducts = List.copyOf(byproducts == null ? List.of() : byproducts);
        if (outputs.isEmpty()) throw new IllegalArgumentException("Recipe must define at least one output");
        repairCostDefinition = repairCostDefinition == null ? RepairCostDefinition.NONE : repairCostDefinition;
    }
}
