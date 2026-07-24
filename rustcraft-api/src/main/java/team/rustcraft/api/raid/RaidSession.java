package team.rustcraft.api.raid;

import java.time.Instant;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.player.PlayerId;

/** In-progress or completed raid tracking session. */
public interface RaidSession {
    RaidSessionId id();
    PlayerId attacker();
    BuildingId targetBuildingId();
    Instant startTime();
    int totalDamage();
    boolean active();
}
