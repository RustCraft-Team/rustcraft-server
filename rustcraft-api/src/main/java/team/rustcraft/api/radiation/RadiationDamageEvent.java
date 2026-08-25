package team.rustcraft.api.radiation;

import java.time.Instant;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.health.HealthProfile;
import team.rustcraft.api.player.PlayerId;

public record RadiationDamageEvent(PlayerId playerId, double radiation, double damage, RadiationProfile radiationProfile, HealthProfile healthProfile, Instant occurredAt) implements Event {}
