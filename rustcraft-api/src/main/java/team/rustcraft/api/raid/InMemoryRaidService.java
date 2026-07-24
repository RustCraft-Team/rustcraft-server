package team.rustcraft.api.raid;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.building.BuildingBlock;
import team.rustcraft.api.building.BuildingBlockId;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.building.BuildingService;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Simple in-memory {@link RaidService}; contains no Minecraft, Fabric, or gameplay registration logic. */
public final class InMemoryRaidService implements RaidService {
    private final Map<DoorId, InMemoryDoor> doors = new LinkedHashMap<>();
    private final Map<LockId, InMemoryLock> locks = new LinkedHashMap<>();
    private final Map<RaidSessionId, InMemoryRaidSession> raids = new LinkedHashMap<>();
    private final BuildingService buildingService; private final EventBus eventBus;
    public InMemoryRaidService(BuildingService buildingService, EventBus eventBus) { this.buildingService=Objects.requireNonNull(buildingService); this.eventBus=Objects.requireNonNull(eventBus); }
    @Override public synchronized Door placeDoor(DoorId id, PlayerId owner, BuildingId buildingId, DoorType type, int maxHealth) { if (doors.containsKey(id)) throw new IllegalArgumentException("Door already exists: "+id.value()); if (buildingService.findBuilding(buildingId).isEmpty()) throw new IllegalArgumentException("Unknown building: "+buildingId.value()); InMemoryDoor d=new InMemoryDoor(id, owner, buildingId, type, maxHealth); doors.put(id,d); eventBus.dispatch(new DoorPlacedEvent(d)); return d; }
    @Override public synchronized boolean destroyDoor(DoorId id) { InMemoryDoor d=doors.remove(Objects.requireNonNull(id)); if (d==null) return false; eventBus.dispatch(new DoorDestroyedEvent(d)); return true; }
    @Override public synchronized Optional<Door> findDoor(DoorId id){return Optional.ofNullable(doors.get(Objects.requireNonNull(id)));}
    @Override public synchronized Door setDoorOpen(DoorId id, boolean open){ InMemoryDoor d=requireDoor(id); d.setOpen(open); return d; }
    @Override public synchronized Door damageDoor(DoorId id, int amount, DamageType damageType){ InMemoryDoor d=requireDoor(id); int applied=d.applyDamage(amount); eventBus.dispatch(BuildingDamagedEvent.door(d.buildingId(), d.id(), applied, Objects.requireNonNull(damageType))); if (d.currentHealth()==0) destroyDoor(id); return d; }
    @Override public synchronized Lock createLock(LockId id, LockType type, PlayerId owner){ if (locks.containsKey(id)) throw new IllegalArgumentException("Lock already exists: "+id.value()); InMemoryLock l=new InMemoryLock(id,type,owner); locks.put(id,l); return l; }
    @Override public synchronized Optional<Lock> findLock(LockId id){return Optional.ofNullable(locks.get(Objects.requireNonNull(id)));}
    @Override public synchronized Door attachLock(DoorId doorId, LockId lockId){ InMemoryDoor d=requireDoor(doorId); InMemoryLock l=requireLock(lockId); d.attachLock(lockId); eventBus.dispatch(new LockAttachedEvent(d,l)); return d; }
    @Override public synchronized boolean removeLock(DoorId doorId){ InMemoryDoor d=requireDoor(doorId); LockId removed=d.removeLock(); if (removed==null) return false; InMemoryLock l=locks.remove(removed); eventBus.dispatch(new LockRemovedEvent(d,l)); return true; }
    @Override public synchronized Lock authorizePlayer(LockId lockId, PlayerId playerId){ InMemoryLock l=requireLock(lockId); l.authorizePlayer(playerId); return l; }
    @Override public synchronized Lock deauthorizePlayer(LockId lockId, PlayerId playerId){ InMemoryLock l=requireLock(lockId); l.deauthorizePlayer(playerId); return l; }
    @Override public synchronized Lock authorizeTeam(LockId lockId, TeamId teamId){ InMemoryLock l=requireLock(lockId); l.authorizeTeam(teamId); return l; }
    @Override public synchronized Lock deauthorizeTeam(LockId lockId, TeamId teamId){ InMemoryLock l=requireLock(lockId); l.deauthorizeTeam(teamId); return l; }
    @Override public synchronized RaidSession startRaid(RaidSessionId id, PlayerId attacker, BuildingId targetBuildingId, Instant startTime){ if (raids.containsKey(id)) throw new IllegalArgumentException("Raid session already exists: "+id.value()); if (buildingService.findBuilding(targetBuildingId).isEmpty()) throw new IllegalArgumentException("Unknown building: "+targetBuildingId.value()); InMemoryRaidSession r=new InMemoryRaidSession(id,attacker,targetBuildingId,startTime); raids.put(id,r); eventBus.dispatch(new RaidStartedEvent(r)); return r; }
    @Override public synchronized RaidSession endRaid(RaidSessionId id){ InMemoryRaidSession r=requireRaid(id); r.end(); eventBus.dispatch(new RaidEndedEvent(r)); return r; }
    @Override public synchronized Optional<RaidSession> findRaidSession(RaidSessionId id){return Optional.ofNullable(raids.get(Objects.requireNonNull(id)));}
    @Override public synchronized RaidSession damageBuildingBlock(RaidSessionId raidSessionId, BuildingBlockId blockId, int amount, DamageType damageType){ InMemoryRaidSession r=requireRaid(raidSessionId); if (!r.active()) throw new IllegalArgumentException("Raid session is not active: "+raidSessionId.value()); BuildingBlock block=buildingService.findBlock(blockId).orElseThrow(() -> new IllegalArgumentException("Unknown building block: "+blockId.value())); if (!block.buildingId().equals(r.targetBuildingId())) throw new IllegalArgumentException("Block is not in target building"); int applied=block.applyDamage(amount); r.addDamage(applied); eventBus.dispatch(BuildingDamagedEvent.block(block.buildingId(), block.id(), r.id(), applied, Objects.requireNonNull(damageType))); if (block.currentHealth()==0) buildingService.destroyBlock(block.id()); return r; }
    private InMemoryDoor requireDoor(DoorId id){ InMemoryDoor d=doors.get(Objects.requireNonNull(id)); if (d==null) throw new IllegalArgumentException("Unknown door: "+id.value()); return d; }
    private InMemoryLock requireLock(LockId id){ InMemoryLock l=locks.get(Objects.requireNonNull(id)); if (l==null) throw new IllegalArgumentException("Unknown lock: "+id.value()); return l; }
    private InMemoryRaidSession requireRaid(RaidSessionId id){ InMemoryRaidSession r=raids.get(Objects.requireNonNull(id)); if (r==null) throw new IllegalArgumentException("Unknown raid session: "+id.value()); return r; }
}
