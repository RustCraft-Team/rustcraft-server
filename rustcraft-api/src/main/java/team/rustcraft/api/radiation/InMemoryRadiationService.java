package team.rustcraft.api.radiation;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.health.DamageType;
import team.rustcraft.api.health.HealthProfile;
import team.rustcraft.api.health.HealthService;
import team.rustcraft.api.health.SimpleDamageSource;
import team.rustcraft.api.player.PlayerId;

public final class InMemoryRadiationService implements RadiationService {
    private final Map<PlayerId, RadiationProfile> profiles = new LinkedHashMap<>();
    private final HealthService healthService;
    private final EventBus eventBus;

    public InMemoryRadiationService(HealthService healthService, EventBus eventBus) {
        this.healthService = Objects.requireNonNull(healthService, "healthService");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override public synchronized RadiationProfile createProfile(PlayerId playerId, double threshold, double damagePerInterval, Instant now) {
        Objects.requireNonNull(playerId, "playerId"); Objects.requireNonNull(now, "now");
        if (profiles.containsKey(playerId)) throw new IllegalArgumentException("Radiation profile already exists: " + playerId.value());
        healthService.findProfile(playerId).orElseThrow(() -> new IllegalArgumentException("Unknown health profile: " + playerId.value()));
        RadiationProfile profile = new RadiationProfile(playerId, 0, threshold, damagePerInterval, now, now);
        profiles.put(playerId, profile);
        return profile;
    }
    @Override public synchronized Optional<RadiationProfile> findProfile(PlayerId playerId) { return Optional.ofNullable(profiles.get(Objects.requireNonNull(playerId, "playerId"))); }
    @Override public synchronized RadiationProfile increase(PlayerId playerId, double amount, Instant now) { return change(playerId, Math.max(0, amount), now); }
    @Override public synchronized RadiationProfile decrease(PlayerId playerId, double amount, Instant now) { return change(playerId, -Math.max(0, amount), now); }

    @Override public synchronized RadiationProfile tick(PlayerId playerId, Duration damageInterval, double decayPerSecond, Instant now) {
        Objects.requireNonNull(damageInterval, "damageInterval");
        if (damageInterval.isZero() || damageInterval.isNegative()) throw new IllegalArgumentException("Damage interval must be positive");
        RadiationProfile before = requireProfile(playerId);
        HealthProfile health = healthService.findProfile(playerId).orElseThrow(() -> new IllegalArgumentException("Unknown health profile: " + playerId.value()));
        double seconds = Math.max(0, Duration.between(before.updatedAt(), now).toMillis() / 1000.0);
        double decayed = Math.max(0, before.currentRadiation() - Math.max(0, decayPerSecond) * seconds);
        RadiationProfile current = replace(before, decayed, before.lastDamageAt(), now);
        if (!health.alive()) return current;
        long intervals = Duration.between(before.lastDamageAt(), now).toMillis() / damageInterval.toMillis();
        if (intervals > 0 && current.aboveThreshold() && current.damagePerInterval() > 0) {
            double damage = intervals * current.damagePerInterval();
            HealthProfile damaged = healthService.damage(playerId, damage, new SimpleDamageSource(DamageType.RADIATION, "radiation"), now);
            current = replace(current, current.currentRadiation(), now, now);
            eventBus.dispatch(new RadiationDamageEvent(playerId, current.currentRadiation(), Math.min(damage, health.currentHealth()), current, damaged, now));
        }
        if (Double.compare(before.currentRadiation(), current.currentRadiation()) != 0) eventBus.dispatch(new RadiationChangedEvent(playerId, before.currentRadiation(), current.currentRadiation(), current, now));
        return current;
    }

    private RadiationProfile change(PlayerId playerId, double delta, Instant now) {
        RadiationProfile before = requireProfile(playerId);
        double nextRadiation = Math.max(0, before.currentRadiation() + delta);
        Instant lastDamageAt = before.lastDamageAt();
        if (before.currentRadiation() < before.threshold() && nextRadiation >= before.threshold()) {
            lastDamageAt = now;
        }
        if (before.currentRadiation() >= before.threshold() && nextRadiation < before.threshold()) {
            lastDamageAt = now;
        }
        RadiationProfile after = replace(before, nextRadiation, lastDamageAt, now);
        if (Double.compare(before.currentRadiation(), after.currentRadiation()) != 0) eventBus.dispatch(new RadiationChangedEvent(playerId, before.currentRadiation(), after.currentRadiation(), after, now));
        return after;
    }
    private RadiationProfile replace(RadiationProfile before, double current, Instant lastDamageAt, Instant now) {
        RadiationProfile profile = new RadiationProfile(before.playerId(), current, before.threshold(), before.damagePerInterval(), lastDamageAt, now);
        profiles.put(before.playerId(), profile); return profile;
    }
    private RadiationProfile requireProfile(PlayerId playerId) { RadiationProfile profile = profiles.get(Objects.requireNonNull(playerId, "playerId")); if (profile == null) throw new IllegalArgumentException("Unknown radiation profile: " + playerId.value()); return profile; }
}
