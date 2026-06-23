package team.rustcraft.api.building;

import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Published when a player is authorized on a tool cupboard. */
public record PlayerAuthorizedEvent(ToolCupboard toolCupboard, PlayerId playerId) implements Event { }
