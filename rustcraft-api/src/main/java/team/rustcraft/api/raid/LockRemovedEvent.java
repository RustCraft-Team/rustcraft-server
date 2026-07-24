package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record LockRemovedEvent(Door door, Lock lock) implements Event {}
