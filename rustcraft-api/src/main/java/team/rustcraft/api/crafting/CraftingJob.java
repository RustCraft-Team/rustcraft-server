package team.rustcraft.api.crafting;

import java.time.Instant;

/** Domain-only queued crafting job. */
public record CraftingJob(
        CraftingJobId id,
        RecipeId recipeId,
        int quantity,
        Instant startedAt,
        Instant completesAt,
        CraftingJobStatus status
) {
    public CraftingJob {
        if (id == null) throw new IllegalArgumentException("Crafting job id must not be null");
        if (recipeId == null) throw new IllegalArgumentException("Recipe id must not be null");
        if (quantity < 1) throw new IllegalArgumentException("Crafting quantity must be at least 1");
        if (startedAt == null) throw new IllegalArgumentException("Started time must not be null");
        if (completesAt == null || completesAt.isBefore(startedAt)) throw new IllegalArgumentException("Completion time must not be before start time");
        if (status == null) throw new IllegalArgumentException("Crafting job status must not be null");
    }

    public CraftingJob complete() { return new CraftingJob(id, recipeId, quantity, startedAt, completesAt, CraftingJobStatus.COMPLETED); }
    public CraftingJob cancel() { return new CraftingJob(id, recipeId, quantity, startedAt, completesAt, CraftingJobStatus.CANCELLED); }
}
