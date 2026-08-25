package team.rustcraft.api.radiation;

import java.time.Instant;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

public record RadiationChangedEvent(PlayerId playerId, double previousRadiation, double newRadiation, RadiationProfile profile, Instant occurredAt) implements Event {}
