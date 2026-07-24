package team.rustcraft.api.crafting;

import team.rustcraft.api.event.Event;

/** Synchronously published when a crafting job completes. */
public record CraftingCompletedEvent(CraftingJob job, CraftingRecipe recipe) implements Event {
    public CraftingCompletedEvent {
        if (job == null) throw new IllegalArgumentException("Crafting job must not be null");
        if (recipe == null) throw new IllegalArgumentException("Crafting recipe must not be null");
    }
}
