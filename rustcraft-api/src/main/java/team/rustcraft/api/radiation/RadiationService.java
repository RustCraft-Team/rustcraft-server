package team.rustcraft.api.radiation;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/** Domain-only radiation service; applies radiation damage through HealthService without platform classes. */
public interface RadiationService {
    RadiationProfile createProfile(PlayerId playerId, double threshold, double damagePerInterval, Instant now);
    Optional<RadiationProfile> findProfile(PlayerId playerId);
    RadiationProfile increase(PlayerId playerId, double amount, Instant now);
    RadiationProfile decrease(PlayerId playerId, double amount, Instant now);
    RadiationProfile tick(PlayerId playerId, Duration damageInterval, double decayPerSecond, Instant now);
}
