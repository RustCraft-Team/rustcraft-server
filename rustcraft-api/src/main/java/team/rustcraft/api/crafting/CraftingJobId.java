package team.rustcraft.api.crafting;

/** Stable RustCraft crafting job identifier. */
public record CraftingJobId(String value) {
    public CraftingJobId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Crafting job id must not be blank");
        }
    }
}
