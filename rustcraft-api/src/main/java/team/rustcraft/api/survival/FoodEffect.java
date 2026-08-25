package team.rustcraft.api.survival;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/** Generic, Minecraft-independent temporary effect applied by consuming food. */
public record FoodEffect(String key, Duration duration, Map<String, Double> attributes) {
    public FoodEffect {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Effect key must not be blank");
        }
        Objects.requireNonNull(duration, "duration");
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Effect duration must be positive");
        }
        attributes = Map.copyOf(Objects.requireNonNull(attributes, "attributes"));
    }
}
