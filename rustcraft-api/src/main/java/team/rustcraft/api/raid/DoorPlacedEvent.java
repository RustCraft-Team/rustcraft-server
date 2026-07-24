package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record DoorPlacedEvent(Door door) implements Event {}
