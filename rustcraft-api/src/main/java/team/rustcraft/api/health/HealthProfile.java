package team.rustcraft.api.health;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/** Immutable health snapshot for a player. Hunger is owned here so survival-food systems never duplicate mutable hunger state. */
public record HealthProfile(PlayerId playerId, double currentHealth, double maxHealth, int bleedingStacks, double hunger, boolean alive, Instant updatedAt) {
    public HealthProfile {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(updatedAt, "updatedAt");
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Max health must be positive");
        }
        currentHealth = clamp(currentHealth, 0, maxHealth);
        bleedingStacks = Math.max(0, bleedingStacks);
        hunger = Math.max(0, hunger);
        alive = alive && currentHealth > 0;
    }

    public static HealthProfile create(PlayerId playerId, double maxHealth, double hunger, Instant now) {
        return new HealthProfile(playerId, maxHealth, maxHealth, 0, hunger, true, now);
    }

    public boolean bleeding() {
        return bleedingStacks > 0;
    }

    public boolean hungry() {
        return hunger <= 0;
    }

    static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
