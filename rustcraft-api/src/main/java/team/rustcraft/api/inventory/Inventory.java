package team.rustcraft.api.inventory;

import java.util.Collection;
import java.util.Optional;
import team.rustcraft.api.team.OwnerRef;

/**
 * RustCraft-domain inventory with a fixed stack slot count.
 */
public interface Inventory {
    InventoryId id();

    OwnerRef owner();

    InventoryType type();

    int slotCount();

    Collection<ItemStack> stacks();

    Optional<ItemStack> findStack(ItemStackId stackId);
}
