package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record LockAttachedEvent(Door door, Lock lock) implements Event {}
