package team.rustcraft.api.raid;

import java.time.Instant;
import java.util.Optional;
import team.rustcraft.api.building.BuildingBlockId;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Domain service for doors, locks, building damage, and raid sessions. */
public interface RaidService {
    Door placeDoor(DoorId id, PlayerId owner, BuildingId buildingId, DoorType type, int maxHealth);
    boolean destroyDoor(DoorId id);
    Optional<Door> findDoor(DoorId id);
    Door setDoorOpen(DoorId id, boolean open);
    Door damageDoor(DoorId id, int amount, DamageType damageType);

    Lock createLock(LockId id, LockType type, PlayerId owner);
    Optional<Lock> findLock(LockId id);
    Door attachLock(DoorId doorId, LockId lockId);
    boolean removeLock(DoorId doorId);
    Lock authorizePlayer(LockId lockId, PlayerId playerId);
    Lock deauthorizePlayer(LockId lockId, PlayerId playerId);
    Lock authorizeTeam(LockId lockId, TeamId teamId);
    Lock deauthorizeTeam(LockId lockId, TeamId teamId);

    RaidSession startRaid(RaidSessionId id, PlayerId attacker, BuildingId targetBuildingId, Instant startTime);
    RaidSession endRaid(RaidSessionId id);
    Optional<RaidSession> findRaidSession(RaidSessionId id);
    RaidSession damageBuildingBlock(RaidSessionId raidSessionId, BuildingBlockId blockId, int amount, DamageType damageType);
}
