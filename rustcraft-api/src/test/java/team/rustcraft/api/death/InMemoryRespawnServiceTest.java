package team.rustcraft.api.death;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;

final class InMemoryRespawnServiceTest {
    @Test
    void createsUnlimitedSleepingBagsAndMultipleBedsForSameOwner() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryRespawnService service = new InMemoryRespawnService(eventBus);
        PlayerId owner = player(1);

        RespawnPoint bagOne = service.createRespawnPoint(new RespawnPointId("bag-1"), owner, position(1), RespawnPointType.SLEEPING_BAG, new MapColor(255, 0, 0), true);
        RespawnPoint bagTwo = service.createRespawnPoint(new RespawnPointId("bag-2"), owner, position(2), RespawnPointType.SLEEPING_BAG, new MapColor(0, 255, 0), true);
        RespawnPoint bedOne = service.createRespawnPoint(new RespawnPointId("bed-1"), owner, position(3), RespawnPointType.BED, new MapColor(0, 0, 255), true);
        RespawnPoint bedTwo = service.createRespawnPoint(new RespawnPointId("bed-2"), owner, position(4), RespawnPointType.BED, new MapColor(255, 255, 255), false);

        assertEquals(List.of(bagOne, bagTwo, bedOne, bedTwo), service.respawnPoints(owner).stream().toList());
        assertEquals(4, events.stream().filter(RespawnPointCreatedEvent.class::isInstance).count());
        assertFalse(bedTwo.active());
    }

    @Test
    void beachRespawnAlwaysExistsWithoutStoredRespawnPoint() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryRespawnService service = new InMemoryRespawnService(eventBus);
        PlayerId owner = player(1);

        RespawnPoint beach = service.beachRespawnPoint(owner);
        RespawnPoint respawned = service.respawn(owner, beach.id());

        assertEquals(RespawnPointType.BEACH, beach.type());
        assertTrue(beach.active());
        assertEquals(owner, beach.owner());
        assertTrue(service.respawnPoints(owner).isEmpty());
        assertEquals(beach, respawned);
        assertTrue(events.stream().anyMatch(PlayerRespawnedEvent.class::isInstance));
    }

    @Test
    void respawnPointsBelongToOwnerAndCanBeDestroyed() {
        InMemoryRespawnService service = new InMemoryRespawnService(new SimpleEventBus());
        PlayerId owner = player(1);
        PlayerId other = player(2);
        RespawnPointId id = new RespawnPointId("owner-bed");
        service.createRespawnPoint(id, owner, position(1), RespawnPointType.BED, new MapColor(100, 100, 100), true);

        assertThrows(IllegalArgumentException.class, () -> service.respawn(other, id));
        assertTrue(service.destroyRespawnPoint(id));
        assertTrue(service.findRespawnPoint(id).isEmpty());
        assertFalse(service.destroyRespawnPoint(id));
    }

    @Test
    void createsCorpseWithOwnerPositionCreationTimeAndDeathEvent() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryRespawnService service = new InMemoryRespawnService(eventBus);
        PlayerId owner = player(1);
        WorldPosition position = position(5);
        Instant createdAt = Instant.parse("2026-06-22T00:00:00Z");

        Corpse corpse = service.createCorpse(new CorpseId("corpse-1"), owner, position, createdAt);

        assertEquals(new CorpseId("corpse-1"), corpse.id());
        assertEquals(owner, corpse.owner());
        assertEquals(position, corpse.position());
        assertEquals(createdAt, corpse.createdAt());
        assertEquals(List.of(corpse), service.corpses(owner).stream().toList());
        assertTrue(events.stream().anyMatch(PlayerDiedEvent.class::isInstance));
    }

    private static WorldPosition position(int x) {
        return new WorldPosition("minecraft:overworld", x, 64, x);
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
