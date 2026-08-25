package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/** Immutable survival-food snapshot. Hunger is mirrored from HealthProfile; HealthService owns that value. */
public record FoodProfile(PlayerId playerId, double hunger, double maxHunger, List<ActiveFoodEffect> activeEffects, Instant updatedAt) {
    public FoodProfile {
        Objects.requireNonNull(playerId, "playerId");
        if (maxHunger <= 0) {
            throw new IllegalArgumentException("Max hunger must be positive");
        }
        hunger = Math.max(0, Math.min(maxHunger, hunger));
        activeEffects = List.copyOf(Objects.requireNonNull(activeEffects, "activeEffects"));
        Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public boolean hungry() {
        return hunger <= 0;
    }
}
