package team.rustcraft.api.blueprint;

import java.util.Optional;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.team.OwnerRef;

/**
 * Immutable in-memory {@link Blueprint} value.
 */
public record InMemoryBlueprint(
        BlueprintId id,
        OwnerRef owner,
        ItemId resultItemId,
        BlueprintState state,
        WorkbenchTier requiredWorkbenchTier,
        int researchScrapCost,
        ItemId optionalFragmentItemId,
        int requiredFragmentCount
) implements Blueprint {
    public InMemoryBlueprint {
        if (id == null) {
            throw new IllegalArgumentException("Blueprint id must not be null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Blueprint owner must not be null");
        }
        if (resultItemId == null) {
            throw new IllegalArgumentException("Blueprint result item id must not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("Blueprint state must not be null");
        }
        if (requiredWorkbenchTier == null) {
            throw new IllegalArgumentException("Required workbench tier must not be null");
        }
        if (researchScrapCost < 0) {
            throw new IllegalArgumentException("Research scrap cost must not be negative");
        }
        if (requiredFragmentCount < 0) {
            throw new IllegalArgumentException("Required fragment count must not be negative");
        }
        if (requiredFragmentCount > 0 && optionalFragmentItemId == null) {
            throw new IllegalArgumentException("Fragment item id is required when fragment count is positive");
        }
    }

    @Override
    public Optional<ItemId> fragmentItemId() {
        return Optional.ofNullable(optionalFragmentItemId);
    }
}
