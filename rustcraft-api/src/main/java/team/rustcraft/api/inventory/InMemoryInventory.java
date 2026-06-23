package team.rustcraft.api.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.team.OwnerRef;

/**
 * Mutable in-memory inventory used by {@link InMemoryInventoryService}.
 */
public final class InMemoryInventory implements Inventory {
    private final InventoryId id;
    private final OwnerRef owner;
    private final InventoryType type;
    private final int slotCount;
    private final Map<ItemStackId, InMemoryItemStack> stacks = new LinkedHashMap<>();

    public InMemoryInventory(InventoryId id, OwnerRef owner, InventoryType type, int slotCount) {
        this.id = Objects.requireNonNull(id, "id");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.type = Objects.requireNonNull(type, "type");
        if (slotCount < 1) {
            throw new IllegalArgumentException("Slot count must be at least 1");
        }
        this.slotCount = slotCount;
    }

    @Override
    public InventoryId id() {
        return id;
    }

    @Override
    public OwnerRef owner() {
        return owner;
    }

    @Override
    public InventoryType type() {
        return type;
    }

    @Override
    public int slotCount() {
        return slotCount;
    }

    @Override
    public Collection<ItemStack> stacks() {
        return new ArrayList<>(stacks.values());
    }

    @Override
    public Optional<ItemStack> findStack(ItemStackId stackId) {
        Objects.requireNonNull(stackId, "stackId");
        return Optional.ofNullable(stacks.get(stackId));
    }

    Map<ItemStackId, InMemoryItemStack> mutableStacks() {
        return stacks;
    }
}
