package team.rustcraft.api.inventory;

import team.rustcraft.api.event.Event;

/** Event published when items are added to an inventory. */
public record ItemAddedEvent(InventoryId inventoryId, ItemStack stack, int amount) implements Event {}
