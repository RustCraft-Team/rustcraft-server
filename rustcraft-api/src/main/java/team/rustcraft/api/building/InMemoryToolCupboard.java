package team.rustcraft.api.building;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** In-memory {@link ToolCupboard} supporting Rust-style owner, player, and team authorization. */
public final class InMemoryToolCupboard implements ToolCupboard {
    private final ToolCupboardId id;
    private final PlayerId owner;
    private final Set<PlayerId> authorizedPlayers = new LinkedHashSet<>();
    private final Set<TeamId> authorizedTeams = new LinkedHashSet<>();
    private final WorldPosition position;
    private final boolean active;

    public InMemoryToolCupboard(ToolCupboardId id, PlayerId owner, WorldPosition position, boolean active) {
        this.id = Objects.requireNonNull(id, "id");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.position = Objects.requireNonNull(position, "position");
        this.active = active;
        this.authorizedPlayers.add(owner);
    }

    @Override public ToolCupboardId id() { return id; }
    @Override public PlayerId owner() { return owner; }
    @Override public Set<PlayerId> authorizedPlayers() { return Set.copyOf(authorizedPlayers); }
    @Override public Set<TeamId> authorizedTeams() { return Set.copyOf(authorizedTeams); }
    @Override public WorldPosition position() { return position; }
    @Override public boolean active() { return active; }
    @Override public boolean isPlayerAuthorized(PlayerId playerId) { return authorizedPlayers.contains(Objects.requireNonNull(playerId, "playerId")); }
    @Override public boolean isTeamAuthorized(TeamId teamId) { return authorizedTeams.contains(Objects.requireNonNull(teamId, "teamId")); }

    boolean authorizePlayer(PlayerId playerId) { return authorizedPlayers.add(Objects.requireNonNull(playerId, "playerId")); }
    boolean deauthorizePlayer(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        if (owner.equals(playerId)) {
            throw new IllegalArgumentException("Tool cupboard owner cannot be deauthorized");
        }
        return authorizedPlayers.remove(playerId);
    }
    boolean authorizeTeam(TeamId teamId) { return authorizedTeams.add(Objects.requireNonNull(teamId, "teamId")); }
    boolean deauthorizeTeam(TeamId teamId) { return authorizedTeams.remove(Objects.requireNonNull(teamId, "teamId")); }
}
