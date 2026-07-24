package team.rustcraft.api.crafting;

import java.util.List;

/** Ordered domain queue of RustCraft crafting jobs. */
public record CraftingQueue(List<CraftingJob> jobs) {
    public CraftingQueue {
        jobs = List.copyOf(jobs == null ? List.of() : jobs);
    }
}
