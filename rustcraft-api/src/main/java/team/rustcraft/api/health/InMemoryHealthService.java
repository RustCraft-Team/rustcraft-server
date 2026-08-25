package team.rustcraft.api.health;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;

/** Simple in-memory {@link HealthService} for tests and local-only usage. */
public final class InMemoryHealthService implements HealthService {
    public static final double DEFAULT_REGEN_PER_SECOND = 0.5;
    public static final double BLEED_DAMAGE_PER_STACK_PER_SECOND = 0.2;

    private final Map<PlayerId, HealthProfile> profiles = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryHealthService(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized HealthProfile createProfile(PlayerId playerId, double maxHealth, double hunger, Instant now) {
        Objects.requireNonNull(playerId, "playerId");
        if (profiles.containsKey(playerId)) {
            throw new IllegalArgumentException("Health profile already exists: " + playerId.value());
        }
        HealthProfile profile = HealthProfile.create(playerId, maxHealth, hunger, now);
        profiles.put(playerId, profile);
        return profile;
    }

    @Override
    public synchronized Optional<HealthProfile> findProfile(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Optional.ofNullable(profiles.get(playerId));
    }

    @Override
    public synchronized HealthProfile damage(PlayerId playerId, double amount, DamageSource source, Instant now) {
        Objects.requireNonNull(source, "source");
        HealthProfile before = requireProfile(playerId);
        if (amount <= 0 || !before.alive()) {
            return before;
        }
        HealthProfile after = replace(before, HealthProfile.clamp(before.currentHealth() - amount, 0, before.maxHealth()), before.maxHealth(), before.bleedingStacks(), before.hunger(), before.alive(), now);
        eventBus.dispatch(new DamageEvent(playerId, Math.min(amount, before.currentHealth()), source, after, now));
        if (before.alive() && !after.alive()) {
            eventBus.dispatch(new PlayerKilledEvent(playerId, source, source.attacker(), now));
        }
        return after;
    }

    @Override
    public synchronized HealthProfile heal(PlayerId playerId, double amount, Instant now) {
        HealthProfile before = requireProfile(playerId);
        if (amount <= 0 || !before.alive()) {
            return before;
        }
        double healed = Math.min(amount, before.maxHealth() - before.currentHealth());
        HealthProfile after = replace(before, before.currentHealth() + healed, before.maxHealth(), before.bleedingStacks(), before.hunger(), true, now);
        if (healed > 0) {
            eventBus.dispatch(new HealEvent(playerId, healed, after, now));
        }
        return after;
    }

    @Override
    public synchronized HealthProfile startBleeding(PlayerId playerId, int stacks, Instant now) {
        HealthProfile before = requireProfile(playerId);
        if (stacks <= 0 || !before.alive()) {
            return before;
        }
        HealthProfile after = replace(before, before.currentHealth(), before.maxHealth(), before.bleedingStacks() + stacks, before.hunger(), true, now);
        eventBus.dispatch(new BleedingStartedEvent(playerId, stacks, after.bleedingStacks(), now));
        return after;
    }

    @Override
    public synchronized HealthProfile stopBleeding(PlayerId playerId, Instant now) {
        HealthProfile before = requireProfile(playerId);
        if (!before.bleeding()) {
            return before;
        }
        HealthProfile after = replace(before, before.currentHealth(), before.maxHealth(), 0, before.hunger(), before.alive(), now);
        eventBus.dispatch(new BleedingStoppedEvent(playerId, before.bleedingStacks(), now));
        return after;
    }

    @Override
    public synchronized HealthProfile setHunger(PlayerId playerId, double hunger, Instant now) {
        HealthProfile before = requireProfile(playerId);
        return replace(before, before.currentHealth(), before.maxHealth(), before.bleedingStacks(), Math.max(0, hunger), before.alive(), now);
    }

    @Override
    public synchronized HealthProfile tick(PlayerId playerId, Instant now) {
        HealthProfile before = requireProfile(playerId);
        double seconds = Math.max(0, Duration.between(before.updatedAt(), now).toMillis() / 1000.0);
        if (seconds == 0 || !before.alive()) {
            return before;
        }
        double environmentalDamage = seconds * before.bleedingStacks() * BLEED_DAMAGE_PER_STACK_PER_SECOND;
        HealthProfile after = before;
        if (environmentalDamage > 0) {
            after = applyTickDamage(before, environmentalDamage, new SimpleDamageSource(DamageType.MELEE, "bleeding"), now);
        }
        if (after.alive() && after.hunger() > 0 && after.currentHealth() < after.maxHealth()) {
            double healed = Math.min(seconds * DEFAULT_REGEN_PER_SECOND, after.maxHealth() - after.currentHealth());
            after = replace(after, after.currentHealth() + healed, after.maxHealth(), after.bleedingStacks(), after.hunger(), true, now);
            eventBus.dispatch(new HealEvent(playerId, healed, after, now));
        } else if (after == before) {
            after = replace(before, before.currentHealth(), before.maxHealth(), before.bleedingStacks(), before.hunger(), before.alive(), now);
        }
        return after;
    }

    private HealthProfile applyTickDamage(HealthProfile before, double amount, DamageSource source, Instant now) {
        HealthProfile after = replace(before, before.currentHealth() - amount, before.maxHealth(), before.bleedingStacks(), before.hunger(), before.alive(), now);
        eventBus.dispatch(new DamageEvent(before.playerId(), Math.min(amount, before.currentHealth()), source, after, now));
        if (before.alive() && !after.alive()) {
            eventBus.dispatch(new PlayerKilledEvent(before.playerId(), source, Optional.empty(), now));
        }
        return after;
    }

    private HealthProfile replace(HealthProfile before, double currentHealth, double maxHealth, int bleedingStacks, double hunger, boolean alive, Instant now) {
        HealthProfile profile = new HealthProfile(before.playerId(), currentHealth, maxHealth, bleedingStacks, hunger, alive, now);
        profiles.put(before.playerId(), profile);
        return profile;
    }

    private HealthProfile requireProfile(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        HealthProfile profile = profiles.get(playerId);
        if (profile == null) {
            throw new IllegalArgumentException("Unknown health profile: " + playerId.value());
        }
        return profile;
    }
}
