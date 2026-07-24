package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record RaidEndedEvent(RaidSession raidSession) implements Event {}
