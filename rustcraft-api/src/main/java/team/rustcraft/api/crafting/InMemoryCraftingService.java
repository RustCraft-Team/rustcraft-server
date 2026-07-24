package team.rustcraft.api.crafting;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import team.rustcraft.api.event.EventBus;

/** In-memory crafting service that performs validation only and dispatches synchronous events. */
public final class InMemoryCraftingService implements CraftingService {
    private final Map<RecipeId, CraftingRecipe> recipes = new LinkedHashMap<>();
    private final Map<CraftingJobId, CraftingJob> jobs = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryCraftingService(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized CraftingRecipe registerRecipe(CraftingRecipe recipe) {
        Objects.requireNonNull(recipe, "recipe");
        if (recipes.containsKey(recipe.id())) throw new IllegalArgumentException("Recipe already exists: " + recipe.id().value());
        recipes.put(recipe.id(), recipe);
        return recipe;
    }

    @Override public synchronized Optional<CraftingRecipe> findRecipe(RecipeId recipeId) { return Optional.ofNullable(recipes.get(Objects.requireNonNull(recipeId, "recipeId"))); }
    @Override public synchronized List<CraftingRecipe> recipes() { return List.copyOf(recipes.values()); }
    @Override public synchronized List<CraftingRecipe> findRecipesByCategory(RecipeCategory category) {
        Objects.requireNonNull(category, "category");
        return recipes.values().stream().filter(recipe -> recipe.category() == category).toList();
    }

    @Override
    public synchronized CraftingJob startCrafting(RecipeId recipeId, int quantity, WorkbenchTier availableWorkbenchTier, boolean blueprintKnown, boolean researchUnlocked, Instant now) {
        if (quantity < 1) throw new IllegalArgumentException("Crafting quantity must be at least 1");
        Objects.requireNonNull(availableWorkbenchTier, "availableWorkbenchTier");
        Objects.requireNonNull(now, "now");
        CraftingRecipe recipe = requireRecipe(recipeId);
        validateUnlocked(recipe, blueprintKnown, researchUnlocked);
        if (availableWorkbenchTier.ordinal() < recipe.workbenchTierRequirement().ordinal()) throw new IllegalArgumentException("Workbench tier requirement not met");
        CraftingJob job = new CraftingJob(new CraftingJobId(UUID.randomUUID().toString()), recipe.id(), quantity, now, now.plus(recipe.craftingTime().multipliedBy(quantity)), CraftingJobStatus.QUEUED);
        jobs.put(job.id(), job);
        eventBus.dispatch(new CraftingStartedEvent(job, recipe));
        return job;
    }

    @Override public synchronized CraftingJob completeCrafting(CraftingJobId jobId) {
        CraftingJob job = requireOpenJob(jobId).complete();
        jobs.put(job.id(), job);
        eventBus.dispatch(new CraftingCompletedEvent(job, requireRecipe(job.recipeId())));
        return job;
    }

    @Override public synchronized CraftingJob cancelCrafting(CraftingJobId jobId) {
        CraftingJob job = requireOpenJob(jobId).cancel();
        jobs.put(job.id(), job);
        eventBus.dispatch(new CraftingCancelledEvent(job, requireRecipe(job.recipeId())));
        return job;
    }

    @Override public synchronized Optional<CraftingJob> findJob(CraftingJobId jobId) { return Optional.ofNullable(jobs.get(Objects.requireNonNull(jobId, "jobId"))); }
    @Override public synchronized CraftingQueue queue() { return new CraftingQueue(jobs.values().stream().filter(job -> job.status() == CraftingJobStatus.QUEUED).toList()); }

    private CraftingRecipe requireRecipe(RecipeId recipeId) {
        Objects.requireNonNull(recipeId, "recipeId");
        CraftingRecipe recipe = recipes.get(recipeId);
        if (recipe == null) throw new IllegalArgumentException("Unknown recipe: " + recipeId.value());
        return recipe;
    }

    private CraftingJob requireOpenJob(CraftingJobId jobId) {
        Objects.requireNonNull(jobId, "jobId");
        CraftingJob job = jobs.get(jobId);
        if (job == null) throw new IllegalArgumentException("Unknown crafting job: " + jobId.value());
        if (job.status() != CraftingJobStatus.QUEUED) throw new IllegalStateException("Crafting job is already closed");
        return job;
    }

    private static void validateUnlocked(CraftingRecipe recipe, boolean blueprintKnown, boolean researchUnlocked) {
        if (!recipe.unlocked()) throw new IllegalStateException("Recipe is locked");
        if (recipe.blueprintRequired() && !blueprintKnown) throw new IllegalArgumentException("Recipe blueprint requirement not met");
        if (recipe.researchRequirement().required() && !researchUnlocked) throw new IllegalArgumentException("Recipe research requirement not met");
    }
}
