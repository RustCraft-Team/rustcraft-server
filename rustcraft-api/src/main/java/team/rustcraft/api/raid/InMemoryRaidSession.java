package team.rustcraft.api.raid;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.player.PlayerId;

/** In-memory {@link RaidSession}. */
public final class InMemoryRaidSession implements RaidSession {
    private final RaidSessionId id; private final PlayerId attacker; private final BuildingId targetBuildingId; private final Instant startTime; private int totalDamage; private boolean active = true;
    public InMemoryRaidSession(RaidSessionId id, PlayerId attacker, BuildingId targetBuildingId, Instant startTime) { this.id=Objects.requireNonNull(id); this.attacker=Objects.requireNonNull(attacker); this.targetBuildingId=Objects.requireNonNull(targetBuildingId); this.startTime=Objects.requireNonNull(startTime); }
    @Override public RaidSessionId id(){return id;} @Override public PlayerId attacker(){return attacker;} @Override public BuildingId targetBuildingId(){return targetBuildingId;} @Override public Instant startTime(){return startTime;} @Override public int totalDamage(){return totalDamage;} @Override public boolean active(){return active;}
    void addDamage(int amount) { totalDamage += amount; }
    void end() { active = false; }
}
