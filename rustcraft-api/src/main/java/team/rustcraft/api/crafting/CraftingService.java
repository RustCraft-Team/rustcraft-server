package team.rustcraft.api.crafting;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Service contract for validating recipes and managing domain-only crafting jobs. */
public interface CraftingService {
    CraftingRecipe registerRecipe(CraftingRecipe recipe);
    Optional<CraftingRecipe> findRecipe(RecipeId recipeId);
    List<CraftingRecipe> recipes();
    List<CraftingRecipe> findRecipesByCategory(RecipeCategory category);
    CraftingJob startCrafting(RecipeId recipeId, int quantity, WorkbenchTier availableWorkbenchTier, boolean blueprintKnown, boolean researchUnlocked, Instant now);
    CraftingJob completeCrafting(CraftingJobId jobId);
    CraftingJob cancelCrafting(CraftingJobId jobId);
    Optional<CraftingJob> findJob(CraftingJobId jobId);
    CraftingQueue queue();
}
