package team.rustcraft.api.status;

import java.time.Duration;
import java.util.Objects;

public record StatusEffectDefinition(StatusEffectId id, StackingPolicy stackingPolicy, RefreshPolicy refreshPolicy, Duration period, PeriodicStatusEffect periodicEffect) {
    public StatusEffectDefinition {
        Objects.requireNonNull(id, "id");
        stackingPolicy = stackingPolicy == null ? StackingPolicy.REPLACE : stackingPolicy;
        refreshPolicy = refreshPolicy == null ? RefreshPolicy.REFRESH_DURATION : refreshPolicy;
        if (period != null && (period.isZero() || period.isNegative())) throw new IllegalArgumentException("Period must be positive");
    }
    public boolean periodic() { return period != null && periodicEffect != null; }
}
