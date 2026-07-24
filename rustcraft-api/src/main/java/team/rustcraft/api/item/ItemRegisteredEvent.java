package team.rustcraft.api.item;

import team.rustcraft.api.event.Event;

/** Published when an item definition is registered. */
public record ItemRegisteredEvent(ItemDefinition definition) implements Event { }
