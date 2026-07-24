package team.rustcraft.api.blueprint;

import java.util.Optional;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.team.OwnerRef;

/**
 * Player-owned RustCraft blueprint knowledge, independent from inventories.
 */
public interface Blueprint {
    BlueprintId id();

    OwnerRef owner();

    ItemId resultItemId();

    BlueprintState state();

    WorkbenchTier requiredWorkbenchTier();

    int researchScrapCost();

    Optional<ItemId> fragmentItemId();

    int requiredFragmentCount();

    default boolean unlocked() {
        return state() == BlueprintState.LEARNED || state() == BlueprintState.DEFAULT_UNLOCKED;
    }
}
