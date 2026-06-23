package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Published when a player is deauthorized from a tool cupboard. */
public record PlayerDeauthorizedEvent(ToolCupboard toolCupboard, PlayerId playerId) implements Event { }
