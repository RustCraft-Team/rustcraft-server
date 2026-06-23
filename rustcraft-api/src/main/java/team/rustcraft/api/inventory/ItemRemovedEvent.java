package team.rustcraft.api.inventory;

import team.rustcraft.api.event.Event;

/** Event published when items are removed from an inventory. */
public record ItemRemovedEvent(InventoryId inventoryId, ItemStack stack, int amount) implements Event {}
