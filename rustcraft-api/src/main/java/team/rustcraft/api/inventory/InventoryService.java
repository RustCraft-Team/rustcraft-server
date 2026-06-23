package team.rustcraft.api.inventory;

import java.util.Optional;
import team.rustcraft.api.team.OwnerRef;

/**
 * Domain service for managing RustCraft inventories and item stacks.
 */
public interface InventoryService {
    Inventory createInventory(InventoryId id, OwnerRef owner, InventoryType type, int slotCount);

    Optional<Inventory> findInventory(InventoryId inventoryId);

    ItemStack addItem(InventoryId inventoryId, Item item, int amount);

    ItemStack removeItem(InventoryId inventoryId, ItemStackId stackId, int amount);

    ItemStack moveItem(InventoryId sourceInventoryId, InventoryId targetInventoryId, ItemStackId stackId, int amount);

    ItemStack splitStack(InventoryId inventoryId, ItemStackId stackId, int amount);

    ItemStack mergeStack(InventoryId inventoryId, ItemStackId sourceStackId, ItemStackId targetStackId);
}
