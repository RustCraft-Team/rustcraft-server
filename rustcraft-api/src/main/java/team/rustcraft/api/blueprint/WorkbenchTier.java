package team.rustcraft.api.blueprint;

/**
 * RustCraft workbench requirement tiers used by crafting recipes.
 */
public enum WorkbenchTier {
    NONE(0),
    TIER_1(1),
    TIER_2(2),
    TIER_3(3);

    private final int level;

    WorkbenchTier(int level) {
        this.level = level;
    }

    public boolean satisfies(WorkbenchTier required) {
        return level >= required.level;
    }
}
