package team.rustcraft.api.inventory;

import team.rustcraft.api.event.Event;

/** Event published when items move between inventories or stacks. */
public record ItemMovedEvent(InventoryId sourceInventoryId, InventoryId targetInventoryId, ItemStack sourceStack, ItemStack targetStack, int amount) implements Event {}
