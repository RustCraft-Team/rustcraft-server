package team.rustcraft.api.raid;

import java.util.Set;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** Minecraft-independent RustCraft lock domain object. */
public interface Lock {
    LockId id();
    LockType type();
    PlayerId owner();
    Set<PlayerId> authorizedPlayers();
    Set<TeamId> authorizedTeams();
    boolean isPlayerAuthorized(PlayerId playerId);
    boolean isTeamAuthorized(TeamId teamId);
}
