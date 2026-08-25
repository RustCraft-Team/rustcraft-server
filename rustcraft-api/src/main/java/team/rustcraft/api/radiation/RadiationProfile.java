package team.rustcraft.api.radiation;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/** Immutable radiation snapshot. RadiationProfile is the only mutable-domain owner of radiation exposure. */
public record RadiationProfile(PlayerId playerId, double currentRadiation, double threshold, double damagePerInterval, Instant lastDamageAt, Instant updatedAt) {
    public RadiationProfile {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(lastDamageAt, "lastDamageAt");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (threshold < 0) throw new IllegalArgumentException("Threshold cannot be negative");
        if (damagePerInterval < 0) throw new IllegalArgumentException("Damage cannot be negative");
        currentRadiation = Math.max(0, currentRadiation);
    }

    public boolean aboveThreshold() {
        return currentRadiation >= threshold && currentRadiation > 0;
    }
}
