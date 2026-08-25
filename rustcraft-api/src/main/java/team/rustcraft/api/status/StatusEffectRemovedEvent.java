package team.rustcraft.api.status;

import java.time.Instant;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

public record StatusEffectRemovedEvent(PlayerId playerId, ActiveStatusEffect effect, RemovalReason reason, Instant occurredAt) implements Event { public enum RemovalReason { EXPIRED, REMOVED, REPLACED } }
