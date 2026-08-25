package team.rustcraft.api.status;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

public record ActiveStatusEffect(PlayerId playerId, StatusEffectId id, int amplifier, Instant startedAt, Instant expiresAt, Instant lastAppliedAt) {
    public ActiveStatusEffect {
        Objects.requireNonNull(playerId, "playerId"); Objects.requireNonNull(id, "id"); Objects.requireNonNull(startedAt, "startedAt"); Objects.requireNonNull(expiresAt, "expiresAt"); Objects.requireNonNull(lastAppliedAt, "lastAppliedAt");
        amplifier = Math.max(0, amplifier);
        if (!expiresAt.isAfter(startedAt)) throw new IllegalArgumentException("Expiration must be after start");
    }
    public boolean activeAt(Instant now) { return now.isBefore(expiresAt); }
}
