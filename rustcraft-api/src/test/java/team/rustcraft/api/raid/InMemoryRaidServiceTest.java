package team.rustcraft.api.raid;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.building.*;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

final class InMemoryRaidServiceTest {
    @Test void placesDoorsAndAppliesDoorDamage() {
        Fixture f = fixture(); DoorId doorId = new DoorId("door-1");
        Door door = f.raids.placeDoor(doorId, f.owner, f.buildingId, DoorType.SHEET_METAL_DOOR, 250);
        f.raids.damageDoor(doorId, 75, DamageType.MELEE);
        assertEquals(f.owner, door.owner()); assertEquals(f.buildingId, door.buildingId()); assertEquals(175, door.currentHealth()); assertFalse(door.isOpen());
        assertTrue(f.events.stream().anyMatch(BuildingDamagedEvent.class::isInstance));
    }

    @Test void supportsKeyAndCodeLockRules() {
        Fixture f = fixture(); DoorId doorId = new DoorId("door-2"); LockId key = new LockId("key-1"); LockId code = new LockId("code-1"); PlayerId friend = player(2); TeamId team = new TeamId("team-a");
        f.raids.placeDoor(doorId, f.owner, f.buildingId, DoorType.ARMORED_DOOR, 800);
        Lock keyLock = f.raids.createLock(key, LockType.KEY_LOCK, f.owner);
        assertTrue(keyLock.isPlayerAuthorized(f.owner)); assertThrows(IllegalArgumentException.class, () -> f.raids.authorizePlayer(key, friend));
        f.raids.createLock(code, LockType.CODE_LOCK, f.owner); f.raids.authorizePlayer(code, friend); f.raids.authorizeTeam(code, team); f.raids.attachLock(doorId, code);
        Door lockedDoor = f.raids.findDoor(doorId).orElseThrow();
        assertEquals(code, lockedDoor.lockId().orElseThrow()); assertTrue(f.raids.findLock(code).orElseThrow().isPlayerAuthorized(friend)); assertTrue(f.raids.findLock(code).orElseThrow().isTeamAuthorized(team));
    }

    @Test void tracksRaidSessionsAndBuildingBlockDamage() {
        Fixture f = fixture(); BuildingBlockId blockId = new BuildingBlockId("wall-1");
        f.buildings.addBlock(blockId, f.buildingId, BuildingBlockType.WALL, BuildingGrade.STONE, 500, new WorldPosition("minecraft:overworld", 0, 64, 0));
        RaidSession raid = f.raids.startRaid(new RaidSessionId("raid-1"), player(99), f.buildingId, Instant.parse("2026-06-23T00:00:00Z"));
        f.raids.damageBuildingBlock(raid.id(), blockId, 125, DamageType.EXPLOSIVE);
        assertEquals(125, raid.totalDamage()); assertEquals(375, f.buildings.findBlock(blockId).orElseThrow().currentHealth());
        f.raids.endRaid(raid.id()); assertFalse(raid.active()); assertThrows(IllegalArgumentException.class, () -> f.raids.damageBuildingBlock(raid.id(), blockId, 1, DamageType.GENERIC));
    }

    @Test void publishesRequestedLifecycleEvents() {
        Fixture f = fixture(); DoorId doorId = new DoorId("door-3"); LockId lockId = new LockId("lock-3"); RaidSessionId raidId = new RaidSessionId("raid-3");
        f.raids.placeDoor(doorId, f.owner, f.buildingId, DoorType.WOOD_DOOR, 100); f.raids.createLock(lockId, LockType.CODE_LOCK, f.owner); f.raids.attachLock(doorId, lockId); f.raids.removeLock(doorId); f.raids.startRaid(raidId, player(3), f.buildingId, Instant.EPOCH); f.raids.endRaid(raidId); f.raids.destroyDoor(doorId);
        assertEquals(List.of(DoorPlacedEvent.class, LockAttachedEvent.class, LockRemovedEvent.class, RaidStartedEvent.class, RaidEndedEvent.class, DoorDestroyedEvent.class), f.events.stream().map(Event::getClass).toList());
    }

    private static Fixture fixture() { SimpleEventBus bus = new SimpleEventBus(); List<Event> events = new ArrayList<>(); bus.subscribe(Event.class, events::add); InMemoryBuildingService buildings = new InMemoryBuildingService(bus); PlayerId owner = player(1); BuildingId buildingId = new BuildingId("base"); buildings.createBuilding(buildingId, owner); events.clear(); return new Fixture(buildings, new InMemoryRaidService(buildings, bus), events, owner, buildingId); }
    private static PlayerId player(int id) { return new PlayerId(new UUID(0, id)); }
    private record Fixture(InMemoryBuildingService buildings, InMemoryRaidService raids, List<Event> events, PlayerId owner, BuildingId buildingId) {}
}
