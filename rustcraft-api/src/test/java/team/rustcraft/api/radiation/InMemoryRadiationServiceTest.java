package team.rustcraft.api.radiation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.health.DamageEvent;
import team.rustcraft.api.health.HealthProfile;
import team.rustcraft.api.health.InMemoryHealthService;
import team.rustcraft.api.health.PlayerKilledEvent;
import team.rustcraft.api.player.PlayerId;

final class InMemoryRadiationServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-25T00:00:00Z");

    @Test void gainsAndLosesRadiationWithEvents() {
        Fixture f = fixture(100, 10);
        assertEquals(25, f.radiation.increase(f.player, 25, NOW).currentRadiation());
        assertEquals(5, f.radiation.decrease(f.player, 20, NOW).currentRadiation());
        assertEquals(0, f.radiation.decrease(f.player, 99, NOW).currentRadiation());
        assertEquals(3, f.events.stream().filter(RadiationChangedEvent.class::isInstance).count());
    }

    @Test void thresholdsGateRadiationDamage() {
        Fixture f = fixture(50, 7);
        f.radiation.increase(f.player, 49, NOW);
        f.radiation.tick(f.player, Duration.ofSeconds(5), 0, NOW.plusSeconds(5));
        assertEquals(100, f.health.findProfile(f.player).orElseThrow().currentHealth());
        f.radiation.increase(f.player, 1, NOW.plusSeconds(6));
        f.radiation.tick(f.player, Duration.ofSeconds(5), 0, NOW.plusSeconds(11));
        assertEquals(93, f.health.findProfile(f.player).orElseThrow().currentHealth());
    }

    @Test void configurableIntervalsApplyMultipleRadiationDamageTicks() {
        Fixture f = fixture(10, 3);
        f.radiation.increase(f.player, 10, NOW);
        f.radiation.tick(f.player, Duration.ofSeconds(2), 0, NOW.plusSeconds(6));
        assertEquals(91, f.health.findProfile(f.player).orElseThrow().currentHealth());
        assertEquals(1, f.events.stream().filter(RadiationDamageEvent.class::isInstance).count());
        assertEquals(1, f.events.stream().filter(DamageEvent.class::isInstance).count());
    }

    @Test void configurableDecayCanPreventThresholdDamage() {
        Fixture f = fixture(10, 10);
        f.radiation.increase(f.player, 12, NOW);
        RadiationProfile profile = f.radiation.tick(f.player, Duration.ofSeconds(5), 1, NOW.plusSeconds(3));
        assertEquals(9, profile.currentRadiation());
        assertEquals(100, f.health.findProfile(f.player).orElseThrow().currentHealth());
    }

    @Test void deadPlayersDoNotReceiveRadiationDamage() {
        Fixture f = fixture(10, 50);
        f.radiation.increase(f.player, 100, NOW);
        f.radiation.tick(f.player, Duration.ofSeconds(1), 0, NOW.plusSeconds(3));
        HealthProfile dead = f.health.findProfile(f.player).orElseThrow();
        assertFalse(dead.alive());
        f.radiation.tick(f.player, Duration.ofSeconds(1), 0, NOW.plusSeconds(6));
        assertEquals(dead, f.health.findProfile(f.player).orElseThrow());
        assertEquals(1, f.events.stream().filter(PlayerKilledEvent.class::isInstance).count());
    }

    private static Fixture fixture(double threshold, double damage) {
        SimpleEventBus bus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        bus.subscribe(Event.class, events::add);
        InMemoryHealthService health = new InMemoryHealthService(bus);
        InMemoryRadiationService radiation = new InMemoryRadiationService(health, bus);
        PlayerId player = player(1);
        health.createProfile(player, 100, 0, NOW);
        radiation.createProfile(player, threshold, damage, NOW);
        return new Fixture(events, health, radiation, player);
    }
    private static PlayerId player(int id) { return new PlayerId(new UUID(0L, id)); }
    private record Fixture(List<Event> events, InMemoryHealthService health, InMemoryRadiationService radiation, PlayerId player) {}
}
