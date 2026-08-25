package team.rustcraft.api.status;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

public interface StatusEffectService {
    StatusEffectDefinition register(StatusEffectDefinition definition);
    Optional<StatusEffectDefinition> findDefinition(StatusEffectId id);
    ActiveStatusEffect apply(PlayerId playerId, StatusEffectId id, Duration duration, int amplifier, Instant now);
    Optional<ActiveStatusEffect> find(PlayerId playerId, StatusEffectId id, Instant now);
    List<ActiveStatusEffect> activeEffects(PlayerId playerId, Instant now);
    void remove(PlayerId playerId, StatusEffectId id, Instant now);
    void tick(PlayerId playerId, Instant now);
}
