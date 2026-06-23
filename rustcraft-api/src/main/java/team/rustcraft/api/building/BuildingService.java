package team.rustcraft.api.building;

import java.util.Collection;
import java.util.Optional;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Domain service for in-memory RustCraft buildings and tool cupboards. */
public interface BuildingService {
    Building createBuilding(BuildingId id, PlayerId owner);

    Optional<Building> findBuilding(BuildingId id);

    boolean destroyBuilding(BuildingId id);

    BuildingBlock addBlock(BuildingBlockId id, BuildingId buildingId, BuildingBlockType type, BuildingGrade grade, int maxHealth, WorldPosition position);

    boolean destroyBlock(BuildingBlockId id);

    Optional<BuildingBlock> findBlock(BuildingBlockId id);

    ToolCupboard placeToolCupboard(ToolCupboardId id, PlayerId owner, WorldPosition position, boolean active);

    boolean destroyToolCupboard(ToolCupboardId id);

    Optional<ToolCupboard> findToolCupboard(ToolCupboardId id);

    Collection<ToolCupboard> toolCupboards();

    ToolCupboard authorizePlayer(ToolCupboardId id, PlayerId playerId);

    ToolCupboard deauthorizePlayer(ToolCupboardId id, PlayerId playerId);

    ToolCupboard authorizeTeam(ToolCupboardId id, TeamId teamId);

    ToolCupboard deauthorizeTeam(ToolCupboardId id, TeamId teamId);
}
