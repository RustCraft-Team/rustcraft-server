package team.rustcraft.api.status;

import java.time.Instant;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

public record StatusEffectAppliedEvent(PlayerId playerId, ActiveStatusEffect effect, Instant occurredAt) implements Event {}
