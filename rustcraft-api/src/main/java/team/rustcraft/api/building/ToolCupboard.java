package team.rustcraft.api.building;

import java.util.Set;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Rust-style authorization source for building access. */
public interface ToolCupboard {
    ToolCupboardId id();

    PlayerId owner();

    Set<PlayerId> authorizedPlayers();

    Set<TeamId> authorizedTeams();

    WorldPosition position();

    boolean active();

    boolean isPlayerAuthorized(PlayerId playerId);

    boolean isTeamAuthorized(TeamId teamId);
}
