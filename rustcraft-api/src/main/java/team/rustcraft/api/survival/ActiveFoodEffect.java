package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/** Active generic food/status effect with a domain timestamp expiry. */
public record ActiveFoodEffect(String key, Instant expiresAt, Map<String, Double> attributes) {
    public ActiveFoodEffect {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Effect key must not be blank");
        }
        Objects.requireNonNull(expiresAt, "expiresAt");
        attributes = Map.copyOf(Objects.requireNonNull(attributes, "attributes"));
    }

    public boolean activeAt(Instant now) {
        Objects.requireNonNull(now, "now");
        return now.isBefore(expiresAt);
    }
}
