package team.rustcraft.api.status;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;

final class InMemoryStatusEffectServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-25T00:00:00Z");
    private final PlayerId player = new PlayerId(new UUID(0L, 1));

    @Test void appliesAndFindsStatusEffects() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:warm");
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.REPLACE, RefreshPolicy.REFRESH_DURATION, null, null));
        ActiveStatusEffect effect = f.service.apply(player, id, Duration.ofSeconds(30), 2, NOW);
        assertEquals(2, effect.amplifier());
        assertEquals(effect, f.service.find(player, id, NOW.plusSeconds(1)).orElseThrow());
        assertTrue(f.events.stream().anyMatch(StatusEffectAppliedEvent.class::isInstance));
    }

    @Test void stacksAmplifiersWhenPolicyAllows() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:boost");
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.STACK_AMPLIFIER, RefreshPolicy.REFRESH_DURATION, null, null));
        f.service.apply(player, id, Duration.ofSeconds(30), 1, NOW);
        ActiveStatusEffect stacked = f.service.apply(player, id, Duration.ofSeconds(30), 2, NOW.plusSeconds(5));
        assertEquals(3, stacked.amplifier());
        assertEquals(NOW.plusSeconds(35), stacked.expiresAt());
    }

    @Test void refreshPolicyCanKeepExistingExpiration() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:slow");
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.REPLACE, RefreshPolicy.KEEP_DURATION, null, null));
        f.service.apply(player, id, Duration.ofSeconds(30), 1, NOW);
        ActiveStatusEffect refreshed = f.service.apply(player, id, Duration.ofSeconds(90), 4, NOW.plusSeconds(5));
        assertEquals(4, refreshed.amplifier());
        assertEquals(NOW.plusSeconds(30), refreshed.expiresAt());
    }

    @Test void effectsExpireAndDispatchRemoval() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:bleed_resist");
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.REPLACE, RefreshPolicy.REFRESH_DURATION, null, null));
        f.service.apply(player, id, Duration.ofSeconds(5), 0, NOW);
        assertTrue(f.service.find(player, id, NOW.plusSeconds(6)).isEmpty());
        StatusEffectRemovedEvent removed = f.events.stream().filter(StatusEffectRemovedEvent.class::isInstance).map(StatusEffectRemovedEvent.class::cast).findFirst().orElseThrow();
        assertEquals(StatusEffectRemovedEvent.RemovalReason.EXPIRED, removed.reason());
    }

    @Test void periodicEffectsExecuteForElapsedIntervals() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:pulse"); AtomicInteger pulses = new AtomicInteger();
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.REPLACE, RefreshPolicy.REFRESH_DURATION, Duration.ofSeconds(2), (effect, now) -> pulses.addAndGet(effect.amplifier() + 1)));
        f.service.apply(player, id, Duration.ofSeconds(10), 1, NOW);
        f.service.tick(player, NOW.plusSeconds(6));
        assertEquals(6, pulses.get());
    }

    @Test void ignorePolicyPreservesOriginalActiveEffect() {
        Fixture f = fixture(); StatusEffectId id = id("rustcraft:unique");
        f.service.register(new StatusEffectDefinition(id, StackingPolicy.IGNORE, RefreshPolicy.REFRESH_DURATION, null, null));
        ActiveStatusEffect first = f.service.apply(player, id, Duration.ofSeconds(10), 1, NOW);
        ActiveStatusEffect ignored = f.service.apply(player, id, Duration.ofSeconds(30), 5, NOW.plusSeconds(1));
        assertEquals(first, ignored);
        assertEquals(1, f.events.stream().filter(StatusEffectAppliedEvent.class::isInstance).count());
    }

    private static StatusEffectId id(String value) { return new StatusEffectId(value); }
    private static Fixture fixture() { SimpleEventBus bus = new SimpleEventBus(); List<Event> events = new ArrayList<>(); bus.subscribe(Event.class, events::add); return new Fixture(new InMemoryStatusEffectService(bus), events); }
    private record Fixture(InMemoryStatusEffectService service, List<Event> events) {}
}
