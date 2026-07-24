package team.rustcraft.api.item;

import team.rustcraft.api.event.Event;

/** Published when an item definition is unregistered. */
public record ItemUnregisteredEvent(ItemDefinition definition) implements Event { }
