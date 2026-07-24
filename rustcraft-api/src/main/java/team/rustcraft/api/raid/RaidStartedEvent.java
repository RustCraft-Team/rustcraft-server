package team.rustcraft.api.raid;
import team.rustcraft.api.event.Event;
public record RaidStartedEvent(RaidSession raidSession) implements Event {}
