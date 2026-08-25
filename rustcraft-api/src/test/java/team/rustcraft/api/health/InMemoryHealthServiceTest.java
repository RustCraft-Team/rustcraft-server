package team.rustcraft.api.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;

final class InMemoryHealthServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-24T00:00:00Z");

    @Test
    void createsHealthProfileWithAliveStateAndStatusValues() {
        InMemoryHealthService service = new InMemoryHealthService(new SimpleEventBus());
        PlayerId player = player(1);

        HealthProfile profile = service.createProfile(player, 100, 250, NOW);

        assertEquals(player, profile.playerId());
        assertEquals(100, profile.currentHealth());
        assertEquals(100, profile.maxHealth());
        assertEquals(0, profile.bleedingStacks());
        assertEquals(250, profile.hunger());
        assertTrue(profile.alive());
        assertFalse(profile.bleeding());
        assertEquals(Optional.of(profile), service.findProfile(player));
        assertThrows(IllegalArgumentException.class, () -> service.createProfile(player, 100, 250, NOW));
    }

    @Test
    void appliesDamageAndDispatchesKilledOnceWhenHealthReachesZero() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryHealthService service = new InMemoryHealthService(eventBus);
        PlayerId player = player(1);
        PlayerId attacker = player(2);
        DamageSource source = new SimpleDamageSource(DamageType.PROJECTILE, Optional.of(attacker), "rifle");
        service.createProfile(player, 100, 100, NOW);

        HealthProfile wounded = service.damage(player, 40, source, NOW.plusSeconds(1));
        HealthProfile killed = service.damage(player, 75, source, NOW.plusSeconds(2));
        HealthProfile unchanged = service.damage(player, 10, source, NOW.plusSeconds(3));

        assertEquals(60, wounded.currentHealth());
        assertEquals(0, killed.currentHealth());
        assertFalse(killed.alive());
        assertEquals(killed, unchanged);
        assertEquals(2, events.stream().filter(DamageEvent.class::isInstance).count());
        assertEquals(1, events.stream().filter(PlayerKilledEvent.class::isInstance).count());
        PlayerKilledEvent killedEvent = events.stream().filter(PlayerKilledEvent.class::isInstance).map(PlayerKilledEvent.class::cast).findFirst().orElseThrow();
        assertEquals(Optional.of(attacker), killedEvent.killer());
    }

    @Test
    void healingRestoresLivingPlayersWithoutExceedingMaximumHealth() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryHealthService service = new InMemoryHealthService(eventBus);
        PlayerId player = player(1);
        service.createProfile(player, 100, 100, NOW);
        service.damage(player, 30, new SimpleDamageSource(DamageType.MELEE, "rock"), NOW);

        HealthProfile healed = service.heal(player, 80, NOW.plusSeconds(1));

        assertEquals(100, healed.currentHealth());
        assertEquals(1, events.stream().filter(HealEvent.class::isInstance).count());
        HealEvent healEvent = events.stream().filter(HealEvent.class::isInstance).map(HealEvent.class::cast).findFirst().orElseThrow();
        assertEquals(30, healEvent.amount());
    }

    @Test
    void bleedingStacksAndOneBandageStopsAllBleeding() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryHealthService service = new InMemoryHealthService(eventBus);
        PlayerId player = player(1);
        service.createProfile(player, 100, 100, NOW);

        HealthProfile bleeding = service.startBleeding(player, 2, NOW.plusSeconds(1));
        bleeding = service.startBleeding(player, 3, NOW.plusSeconds(2));
        HealthProfile bandaged = service.stopBleeding(player, NOW.plusSeconds(3));

        assertEquals(5, bleeding.bleedingStacks());
        assertTrue(bleeding.bleeding());
        assertEquals(0, bandaged.bleedingStacks());
        assertFalse(bandaged.bleeding());
        assertEquals(2, events.stream().filter(BleedingStartedEvent.class::isInstance).count());
        BleedingStoppedEvent stopped = events.stream().filter(BleedingStoppedEvent.class::isInstance).map(BleedingStoppedEvent.class::cast).findFirst().orElseThrow();
        assertEquals(5, stopped.stacksRemoved());
    }

    @Test
    void bleedingDamagesHealthOverTime() {
        InMemoryHealthService service = new InMemoryHealthService(new SimpleEventBus());
        PlayerId player = player(1);
        service.createProfile(player, 100, 0, NOW);
        service.startBleeding(player, 2, NOW);
        HealthProfile profile = service.tick(player, NOW.plusSeconds(10));

        assertEquals(96, profile.currentHealth());
    }

    @Test
    void hungerPreventsRegenerationExactlyWithoutThirstRequirement() {
        InMemoryHealthService service = new InMemoryHealthService(new SimpleEventBus());
        PlayerId player = player(1);
        service.createProfile(player, 100, 0, NOW);
        service.damage(player, 20, new SimpleDamageSource(DamageType.FALL, "fall"), NOW);

        HealthProfile hungry = service.tick(player, NOW.plusSeconds(10));
        HealthProfile fed = service.setHunger(player, 10, NOW.plusSeconds(10));
        fed = service.tick(player, NOW.plusSeconds(20));

        assertEquals(80, hungry.currentHealth());
        assertTrue(hungry.hungry());
        assertEquals(85, fed.currentHealth());
    }

    @Test
    void supportsAllDamageTypesAndRejectsUnknownProfiles() {
        for (DamageType type : DamageType.values()) {
            assertTrue(List.of(DamageType.MELEE, DamageType.PROJECTILE, DamageType.EXPLOSION, DamageType.FIRE, DamageType.RADIATION, DamageType.FALL, DamageType.ANIMAL).contains(type));
        }
        InMemoryHealthService service = new InMemoryHealthService(new SimpleEventBus());
        assertThrows(IllegalArgumentException.class, () -> service.damage(player(99), 1, new SimpleDamageSource(DamageType.ANIMAL, "bear"), NOW));
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
