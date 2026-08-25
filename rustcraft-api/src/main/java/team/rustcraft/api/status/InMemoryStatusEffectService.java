package team.rustcraft.api.status;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.status.StatusEffectRemovedEvent.RemovalReason;

public final class InMemoryStatusEffectService implements StatusEffectService {
    private final Map<StatusEffectId, StatusEffectDefinition> definitions = new LinkedHashMap<>();
    private final Map<PlayerId, Map<StatusEffectId, ActiveStatusEffect>> active = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryStatusEffectService(EventBus eventBus) { this.eventBus = Objects.requireNonNull(eventBus, "eventBus"); }

    @Override public synchronized StatusEffectDefinition register(StatusEffectDefinition definition) { definitions.put(definition.id(), definition); return definition; }
    @Override public synchronized Optional<StatusEffectDefinition> findDefinition(StatusEffectId id) { return Optional.ofNullable(definitions.get(id)); }

    @Override public synchronized ActiveStatusEffect apply(PlayerId playerId, StatusEffectId id, Duration duration, int amplifier, Instant now) {
        validate(duration, now);
        StatusEffectDefinition definition = requireDefinition(id);
        Map<StatusEffectId, ActiveStatusEffect> playerEffects = active.computeIfAbsent(playerId, ignored -> new LinkedHashMap<>());
        ActiveStatusEffect existing = playerEffects.get(id);
        if (existing != null && !existing.activeAt(now)) existing = expire(playerId, id, now);
        if (existing != null && definition.stackingPolicy() == StackingPolicy.IGNORE) return existing;
        Instant started = existing == null ? now : existing.startedAt();
        Instant expires = existing != null && definition.refreshPolicy() == RefreshPolicy.KEEP_DURATION ? existing.expiresAt() : now.plus(duration);
        int newAmplifier = amplifier;
        if (existing != null && definition.stackingPolicy() == StackingPolicy.STACK_AMPLIFIER) newAmplifier = existing.amplifier() + Math.max(0, amplifier);
        ActiveStatusEffect effect = new ActiveStatusEffect(playerId, id, newAmplifier, started, expires, existing == null ? now : existing.lastAppliedAt());
        if (existing != null && definition.stackingPolicy() == StackingPolicy.REPLACE) eventBus.dispatch(new StatusEffectRemovedEvent(playerId, existing, RemovalReason.REPLACED, now));
        playerEffects.put(id, effect);
        eventBus.dispatch(new StatusEffectAppliedEvent(playerId, effect, now));
        return effect;
    }

    @Override public synchronized Optional<ActiveStatusEffect> find(PlayerId playerId, StatusEffectId id, Instant now) { tick(playerId, now); return Optional.ofNullable(active.getOrDefault(playerId, Map.of()).get(id)); }
    @Override public synchronized List<ActiveStatusEffect> activeEffects(PlayerId playerId, Instant now) { tick(playerId, now); return List.copyOf(active.getOrDefault(playerId, Map.of()).values()); }
    @Override public synchronized void remove(PlayerId playerId, StatusEffectId id, Instant now) {
        Map<StatusEffectId, ActiveStatusEffect> playerEffects = active.get(playerId);
        if (playerEffects == null) return;
        ActiveStatusEffect removed = playerEffects.remove(id);
        if (removed != null) eventBus.dispatch(new StatusEffectRemovedEvent(playerId, removed, RemovalReason.REMOVED, now));
    }

    @Override public synchronized void tick(PlayerId playerId, Instant now) {
        Map<StatusEffectId, ActiveStatusEffect> playerEffects = active.get(playerId);
        if (playerEffects == null) return;
        for (StatusEffectId id : new ArrayList<>(playerEffects.keySet())) {
            ActiveStatusEffect effect = playerEffects.get(id);
            if (!effect.activeAt(now)) { expire(playerId, id, now); continue; }
            StatusEffectDefinition definition = definitions.get(id);
            if (definition != null && definition.periodic()) {
                long intervals = Duration.between(effect.lastAppliedAt(), now).toMillis() / definition.period().toMillis();
                if (intervals > 0) {
                    for (long i = 0; i < intervals; i++) definition.periodicEffect().apply(effect, now);
                    playerEffects.put(id, new ActiveStatusEffect(playerId, id, effect.amplifier(), effect.startedAt(), effect.expiresAt(), effect.lastAppliedAt().plus(definition.period().multipliedBy(intervals))));
                }
            }
        }
    }
    private ActiveStatusEffect expire(PlayerId playerId, StatusEffectId id, Instant now) {
        Map<StatusEffectId, ActiveStatusEffect> playerEffects = active.get(playerId);
        ActiveStatusEffect removed = playerEffects == null ? null : playerEffects.remove(id);
        if (removed != null) eventBus.dispatch(new StatusEffectRemovedEvent(playerId, removed, RemovalReason.EXPIRED, now));
        return null;
    }
    private StatusEffectDefinition requireDefinition(StatusEffectId id) { StatusEffectDefinition d = definitions.get(Objects.requireNonNull(id, "id")); if (d == null) throw new IllegalArgumentException("Unknown status effect: " + id.value()); return d; }
    private static void validate(Duration duration, Instant now) { Objects.requireNonNull(duration, "duration"); Objects.requireNonNull(now, "now"); if (duration.isZero() || duration.isNegative()) throw new IllegalArgumentException("Duration must be positive"); }
}
