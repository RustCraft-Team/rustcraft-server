package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;

/** Published when a building is created. */
public record BuildingCreatedEvent(Building building) implements Event { }
