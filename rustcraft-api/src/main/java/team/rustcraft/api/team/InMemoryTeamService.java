package team.rustcraft.api.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.player.PlayerId;

/**
 * Simple in-memory {@link TeamService} for tests and local-only usage.
 */
public final class InMemoryTeamService implements TeamService {
    private final Map<TeamId, InMemoryTeam> teams = new LinkedHashMap<>();
    private final int maxTeamSize;
    private final EventBus eventBus;

    public InMemoryTeamService(int maxTeamSize, EventBus eventBus) {
        if (maxTeamSize < 1) {
            throw new IllegalArgumentException("Max team size must be at least 1");
        }
        this.maxTeamSize = maxTeamSize;
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized Team createTeam(TeamId id, String name, PlayerId leaderId, boolean friendlyFireDisabled, Map<String, String> tags) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(leaderId, "leaderId");
        if (teams.containsKey(id)) {
            throw new IllegalArgumentException("Team already exists: " + id.value());
        }
        if (findTeamByPlayer(leaderId).isPresent()) {
            throw new IllegalArgumentException("Player already belongs to a team");
        }
        InMemoryTeam team = new InMemoryTeam(id, name, leaderId, Set.of(leaderId), tags, friendlyFireDisabled, maxTeamSize);
        teams.put(id, team);
        eventBus.dispatch(new TeamCreatedEvent(team));
        eventBus.dispatch(new TeamMemberJoinedEvent(team, leaderId, TeamRole.LEADER));
        return team;
    }

    @Override
    public synchronized boolean disbandTeam(TeamId teamId) {
        Objects.requireNonNull(teamId, "teamId");
        InMemoryTeam removed = teams.remove(teamId);
        if (removed == null) {
            return false;
        }
        eventBus.dispatch(new TeamDisbandedEvent(removed));
        return true;
    }

    @Override
    public synchronized Team joinTeam(TeamId teamId, PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        InMemoryTeam team = requireTeam(teamId);
        if (findTeamByPlayer(playerId).isPresent()) {
            throw new IllegalArgumentException("Player already belongs to a team");
        }
        if (team.members().size() >= team.maxSize()) {
            throw new IllegalStateException("Team is full");
        }
        Set<PlayerId> members = new LinkedHashSet<>(team.members());
        members.add(playerId);
        InMemoryTeam updated = copyWithMembers(team, members);
        teams.put(teamId, updated);
        eventBus.dispatch(new TeamMemberJoinedEvent(updated, playerId, TeamRole.MEMBER));
        return updated;
    }

    @Override
    public synchronized Team leaveTeam(TeamId teamId, PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        InMemoryTeam team = requireTeam(teamId);
        if (!team.members().contains(playerId)) {
            throw new IllegalArgumentException("Player is not a team member");
        }
        TeamRole previousRole = team.roleOf(playerId);
        if (previousRole == TeamRole.LEADER) {
            throw new IllegalArgumentException("Leader cannot leave without disbanding the team");
        }
        Set<PlayerId> members = new LinkedHashSet<>(team.members());
        members.remove(playerId);
        InMemoryTeam updated = copyWithMembers(team, members);
        teams.put(teamId, updated);
        eventBus.dispatch(new TeamMemberLeftEvent(updated, playerId, previousRole));
        return updated;
    }

    @Override
    public synchronized Optional<Team> findTeam(TeamId teamId) {
        Objects.requireNonNull(teamId, "teamId");
        return Optional.ofNullable(teams.get(teamId));
    }

    @Override
    public synchronized Optional<Team> findTeamByPlayer(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return teams.values().stream().filter(team -> team.members().contains(playerId)).map(Team.class::cast).findFirst();
    }

    @Override
    public synchronized Collection<Team> teams() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public int maxTeamSize() {
        return maxTeamSize;
    }

    private InMemoryTeam requireTeam(TeamId teamId) {
        Objects.requireNonNull(teamId, "teamId");
        InMemoryTeam team = teams.get(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Unknown team: " + teamId.value());
        }
        return team;
    }

    private static InMemoryTeam copyWithMembers(InMemoryTeam team, Set<PlayerId> members) {
        return new InMemoryTeam(team.id(), team.name(), team.leaderId(), members, team.tags(), team.friendlyFireDisabled(), team.maxSize());
    }
}
