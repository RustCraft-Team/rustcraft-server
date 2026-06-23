package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;

/** Published when a building is destroyed. */
public record BuildingDestroyedEvent(Building building) implements Event { }
