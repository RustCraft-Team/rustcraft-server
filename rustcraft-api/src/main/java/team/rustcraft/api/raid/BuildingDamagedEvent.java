package team.rustcraft.api.raid;

import team.rustcraft.api.building.BuildingBlockId;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.event.Event;

public record BuildingDamagedEvent(BuildingId buildingId, BuildingBlockId blockId, DoorId doorId, RaidSessionId raidSessionId, int amount, DamageType damageType) implements Event {
    public static BuildingDamagedEvent block(BuildingId buildingId, BuildingBlockId blockId, RaidSessionId raidSessionId, int amount, DamageType damageType) { return new BuildingDamagedEvent(buildingId, blockId, null, raidSessionId, amount, damageType); }
    public static BuildingDamagedEvent door(BuildingId buildingId, DoorId doorId, int amount, DamageType damageType) { return new BuildingDamagedEvent(buildingId, null, doorId, null, amount, damageType); }
}
