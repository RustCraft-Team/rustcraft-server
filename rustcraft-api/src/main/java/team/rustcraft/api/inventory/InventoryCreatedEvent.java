package team.rustcraft.api.inventory;

import team.rustcraft.api.event.Event;

/** Event published when an inventory is created. */
public record InventoryCreatedEvent(Inventory inventory) implements Event {}
