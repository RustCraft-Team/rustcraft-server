package team.rustcraft.api.blueprint;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.team.OwnerRef;

/**
 * Synchronous in-memory {@link BlueprintService} implementation for tests and local usage.
 */
public final class InMemoryBlueprintService implements BlueprintService {
    private final Map<BlueprintId, InMemoryBlueprint> blueprints = new LinkedHashMap<>();

    @Override
    public synchronized Blueprint createLockedBlueprint(BlueprintId id, OwnerRef owner, ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier, int researchScrapCost) {
        return put(new InMemoryBlueprint(id, owner, resultItemId, BlueprintState.LOCKED, requiredWorkbenchTier,
                researchScrapCost, null, 0));
    }

    @Override
    public synchronized Blueprint createDefaultUnlockedBlueprint(BlueprintId id, OwnerRef owner, ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier) {
        return put(new InMemoryBlueprint(id, owner, resultItemId, BlueprintState.DEFAULT_UNLOCKED, requiredWorkbenchTier,
                0, null, 0));
    }

    @Override
    public synchronized Blueprint createFragmentBlueprint(BlueprintId id, OwnerRef owner, ItemId resultItemId,
            WorkbenchTier requiredWorkbenchTier, int researchScrapCost, ItemId fragmentItemId, int requiredFragmentCount) {
        return put(new InMemoryBlueprint(id, owner, resultItemId, BlueprintState.LOCKED, requiredWorkbenchTier,
                researchScrapCost, fragmentItemId, requiredFragmentCount));
    }

    @Override
    public synchronized Optional<Blueprint> findBlueprint(BlueprintId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(blueprints.get(id));
    }

    @Override
    public synchronized Blueprint learnBlueprint(BlueprintId id) {
        InMemoryBlueprint blueprint = requireBlueprint(id);
        InMemoryBlueprint learned = new InMemoryBlueprint(blueprint.id(), blueprint.owner(), blueprint.resultItemId(),
                BlueprintState.LEARNED, blueprint.requiredWorkbenchTier(), blueprint.researchScrapCost(),
                blueprint.fragmentItemId().orElse(null), blueprint.requiredFragmentCount());
        blueprints.put(id, learned);
        return learned;
    }

    @Override
    public synchronized Blueprint lockBlueprint(BlueprintId id) {
        InMemoryBlueprint blueprint = requireBlueprint(id);
        InMemoryBlueprint locked = new InMemoryBlueprint(blueprint.id(), blueprint.owner(), blueprint.resultItemId(),
                BlueprintState.LOCKED, blueprint.requiredWorkbenchTier(), blueprint.researchScrapCost(),
                blueprint.fragmentItemId().orElse(null), blueprint.requiredFragmentCount());
        blueprints.put(id, locked);
        return locked;
    }

    @Override
    public synchronized boolean canCraft(BlueprintId id, WorkbenchTier nearbyWorkbenchTier) {
        Objects.requireNonNull(nearbyWorkbenchTier, "nearbyWorkbenchTier");
        InMemoryBlueprint blueprint = requireBlueprint(id);
        return blueprint.unlocked() && nearbyWorkbenchTier.satisfies(blueprint.requiredWorkbenchTier());
    }

    private InMemoryBlueprint put(InMemoryBlueprint blueprint) {
        if (blueprints.containsKey(blueprint.id())) {
            throw new IllegalArgumentException("Blueprint already exists: " + blueprint.id().value());
        }
        blueprints.put(blueprint.id(), blueprint);
        return blueprint;
    }

    private InMemoryBlueprint requireBlueprint(BlueprintId id) {
        Objects.requireNonNull(id, "id");
        InMemoryBlueprint blueprint = blueprints.get(id);
        if (blueprint == null) {
            throw new IllegalArgumentException("Unknown blueprint: " + id.value());
        }
        return blueprint;
    }
}
