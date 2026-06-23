package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;

/** Published when a tool cupboard is destroyed. */
public record ToolCupboardDestroyedEvent(ToolCupboard toolCupboard) implements Event { }
