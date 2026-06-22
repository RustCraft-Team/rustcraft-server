package team.rustcraft.api.team;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/**
 * Service contract for managing the RustCraft team domain model.
 */
public interface TeamService {
    Team createTeam(TeamId id, String name, PlayerId leaderId, boolean friendlyFireDisabled, Map<String, String> tags);

    boolean disbandTeam(TeamId teamId);

    Team joinTeam(TeamId teamId, PlayerId playerId);

    Team leaveTeam(TeamId teamId, PlayerId playerId);

    Optional<Team> findTeam(TeamId teamId);

    Optional<Team> findTeamByPlayer(PlayerId playerId);

    Collection<Team> teams();

    int maxTeamSize();
}
