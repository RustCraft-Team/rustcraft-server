package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record DoorDestroyedEvent(Door door) implements Event {}
