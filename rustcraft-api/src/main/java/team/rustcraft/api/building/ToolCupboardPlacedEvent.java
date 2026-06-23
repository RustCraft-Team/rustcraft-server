package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;

/** Published when a tool cupboard is placed. */
public record ToolCupboardPlacedEvent(ToolCupboard toolCupboard) implements Event { }
