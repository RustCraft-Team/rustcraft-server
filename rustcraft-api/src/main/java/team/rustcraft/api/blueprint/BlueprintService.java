package team.rustcraft.api.blueprint;

import java.util.Optional;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.team.OwnerRef;

/**
 * Domain service for managing player-owned RustCraft blueprint knowledge.
 */
public interface BlueprintService {
    Blueprint createLockedBlueprint(
            BlueprintId id,
            OwnerRef owner,
            ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier,
            int researchScrapCost
    );

    Blueprint createDefaultUnlockedBlueprint(
            BlueprintId id,
            OwnerRef owner,
            ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier
    );

    Blueprint createFragmentBlueprint(
            BlueprintId id,
            OwnerRef owner,
            ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier,
            int researchScrapCost,
            ItemId fragmentItemId,
            int requiredFragmentCount
    );

    Optional<Blueprint> findBlueprint(BlueprintId id);

    Blueprint learnBlueprint(BlueprintId id);

    Blueprint lockBlueprint(BlueprintId id);

    boolean canCraft(BlueprintId id, WorkbenchTier nearbyWorkbenchTier);
}
