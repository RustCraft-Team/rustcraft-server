package team.rustcraft.api.inventory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.team.OwnerRef;

/**
 * Synchronous in-memory {@link InventoryService} implementation for tests and local usage.
 */
public final class InMemoryInventoryService implements InventoryService {
    private final Map<InventoryId, InMemoryInventory> inventories = new LinkedHashMap<>();
    private final Map<ItemId, Item> items = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryInventoryService(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized Inventory createInventory(InventoryId id, OwnerRef owner, InventoryType type, int slotCount) {
        Objects.requireNonNull(id, "id");
        if (inventories.containsKey(id)) {
            throw new IllegalArgumentException("Inventory already exists: " + id.value());
        }
        InMemoryInventory inventory = new InMemoryInventory(id, owner, type, slotCount);
        inventories.put(id, inventory);
        eventBus.dispatch(new InventoryCreatedEvent(inventory));
        return inventory;
    }

    @Override
    public synchronized Optional<Inventory> findInventory(InventoryId inventoryId) {
        Objects.requireNonNull(inventoryId, "inventoryId");
        return Optional.ofNullable(inventories.get(inventoryId));
    }

    @Override
    public synchronized ItemStack addItem(InventoryId inventoryId, Item item, int amount) {
        validateAmount(amount);
        Objects.requireNonNull(item, "item");
        items.putIfAbsent(item.id(), item);
        InMemoryInventory inventory = requireInventory(inventoryId);
        InMemoryItemStack stack = putNewStack(inventory, item.id(), amount);
        eventBus.dispatch(new ItemAddedEvent(inventory.id(), stack, amount));
        return stack;
    }

    @Override
    public synchronized ItemStack removeItem(InventoryId inventoryId, ItemStackId stackId, int amount) {
        validateAmount(amount);
        InMemoryInventory inventory = requireInventory(inventoryId);
        InMemoryItemStack stack = requireStack(inventory, stackId);
        if (amount > stack.amount()) {
            throw new IllegalArgumentException("Cannot remove more items than the stack contains");
        }
        InMemoryItemStack removed = new InMemoryItemStack(stack.id(), stack.itemId(), amount);
        if (amount == stack.amount()) {
            inventory.mutableStacks().remove(stackId);
        } else {
            inventory.mutableStacks().put(stackId, new InMemoryItemStack(stack.id(), stack.itemId(), stack.amount() - amount));
        }
        eventBus.dispatch(new ItemRemovedEvent(inventory.id(), removed, amount));
        return removed;
    }

    @Override
    public synchronized ItemStack moveItem(InventoryId sourceInventoryId, InventoryId targetInventoryId, ItemStackId stackId, int amount) {
        validateAmount(amount);
        InMemoryInventory source = requireInventory(sourceInventoryId);
        InMemoryInventory target = requireInventory(targetInventoryId);
        InMemoryItemStack sourceStack = requireStack(source, stackId);
        if (amount > sourceStack.amount()) {
            throw new IllegalArgumentException("Cannot move more items than the stack contains");
        }
        Item item = requireItem(sourceStack.itemId());
        ensureFits(item, amount);
        if (target.mutableStacks().size() >= target.slotCount()) {
            throw new IllegalStateException("Inventory is full");
        }
        if (amount == sourceStack.amount()) {
            source.mutableStacks().remove(stackId);
        } else {
            source.mutableStacks().put(stackId, new InMemoryItemStack(stackId, sourceStack.itemId(), sourceStack.amount() - amount));
        }
        InMemoryItemStack moved = putNewStack(target, sourceStack.itemId(), amount);
        eventBus.dispatch(new ItemMovedEvent(source.id(), target.id(), sourceStack, moved, amount));
        return moved;
    }

    @Override
    public synchronized ItemStack splitStack(InventoryId inventoryId, ItemStackId stackId, int amount) {
        validateAmount(amount);
        InMemoryInventory inventory = requireInventory(inventoryId);
        InMemoryItemStack source = requireStack(inventory, stackId);
        if (amount >= source.amount()) {
            throw new IllegalArgumentException("Split amount must be less than the source stack amount");
        }
        if (inventory.mutableStacks().size() >= inventory.slotCount()) {
            throw new IllegalStateException("Inventory is full");
        }
        inventory.mutableStacks().put(stackId, new InMemoryItemStack(stackId, source.itemId(), source.amount() - amount));
        InMemoryItemStack split = putNewStack(inventory, source.itemId(), amount);
        eventBus.dispatch(new ItemMovedEvent(inventory.id(), inventory.id(), source, split, amount));
        return split;
    }

    @Override
    public synchronized ItemStack mergeStack(InventoryId inventoryId, ItemStackId sourceStackId, ItemStackId targetStackId) {
        InMemoryInventory inventory = requireInventory(inventoryId);
        InMemoryItemStack source = requireStack(inventory, sourceStackId);
        InMemoryItemStack target = requireStack(inventory, targetStackId);
        if (sourceStackId.equals(targetStackId)) {
            throw new IllegalArgumentException("Cannot merge a stack into itself");
        }
        if (!source.itemId().equals(target.itemId())) {
            throw new IllegalArgumentException("Only stacks of the same item can be merged");
        }
        Item item = requireItem(source.itemId());
        int mergedAmount = source.amount() + target.amount();
        ensureFits(item, mergedAmount);
        InMemoryItemStack merged = new InMemoryItemStack(target.id(), target.itemId(), mergedAmount);
        inventory.mutableStacks().remove(sourceStackId);
        inventory.mutableStacks().put(targetStackId, merged);
        eventBus.dispatch(new ItemMovedEvent(inventory.id(), inventory.id(), source, merged, source.amount()));
        return merged;
    }

    private InMemoryInventory requireInventory(InventoryId inventoryId) {
        Objects.requireNonNull(inventoryId, "inventoryId");
        InMemoryInventory inventory = inventories.get(inventoryId);
        if (inventory == null) {
            throw new IllegalArgumentException("Unknown inventory: " + inventoryId.value());
        }
        return inventory;
    }

    private InMemoryItemStack requireStack(InMemoryInventory inventory, ItemStackId stackId) {
        Objects.requireNonNull(stackId, "stackId");
        InMemoryItemStack stack = inventory.mutableStacks().get(stackId);
        if (stack == null) {
            throw new IllegalArgumentException("Unknown stack: " + stackId.value());
        }
        return stack;
    }

    private Item requireItem(ItemId itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item: " + itemId.value());
        }
        return item;
    }

    private InMemoryItemStack putNewStack(InMemoryInventory inventory, ItemId itemId, int amount) {
        Item item = requireItem(itemId);
        ensureFits(item, amount);
        if (inventory.mutableStacks().size() >= inventory.slotCount()) {
            throw new IllegalStateException("Inventory is full");
        }
        InMemoryItemStack stack = new InMemoryItemStack(new ItemStackId(UUID.randomUUID().toString()), itemId, amount);
        inventory.mutableStacks().put(stack.id(), stack);
        return stack;
    }

    private static void validateAmount(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be at least 1");
        }
    }

    private static void ensureFits(Item item, int amount) {
        if (amount > item.maxStackSize()) {
            throw new IllegalArgumentException("Amount exceeds max stack size");
        }
    }
}
