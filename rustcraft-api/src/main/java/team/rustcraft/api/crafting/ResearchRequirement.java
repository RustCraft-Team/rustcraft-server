package team.rustcraft.api.crafting;

/** Scrap research cost required to unlock a RustCraft crafting recipe. */
public record ResearchRequirement(int scrapCost) {
    public static final ResearchRequirement NONE = new ResearchRequirement(0);

    public ResearchRequirement {
        if (scrapCost < 0) {
            throw new IllegalArgumentException("Research scrap cost must not be negative");
        }
    }

    public boolean required() {
        return scrapCost > 0;
    }
}
