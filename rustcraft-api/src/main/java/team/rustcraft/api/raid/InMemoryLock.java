package team.rustcraft.api.raid;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

/** In-memory {@link Lock}. */
public final class InMemoryLock implements Lock {
    private final LockId id; private final LockType type; private final PlayerId owner; private final Set<PlayerId> players = new LinkedHashSet<>(); private final Set<TeamId> teams = new LinkedHashSet<>();
    public InMemoryLock(LockId id, LockType type, PlayerId owner) { this.id=Objects.requireNonNull(id); this.type=Objects.requireNonNull(type); this.owner=Objects.requireNonNull(owner); players.add(owner); }
    @Override public LockId id() { return id; }
    @Override public LockType type() { return type; }
    @Override public PlayerId owner() { return owner; }
    @Override public Set<PlayerId> authorizedPlayers() { return Collections.unmodifiableSet(players); }
    @Override public Set<TeamId> authorizedTeams() { return Collections.unmodifiableSet(teams); }
    @Override public boolean isPlayerAuthorized(PlayerId playerId) { return players.contains(playerId); }
    @Override public boolean isTeamAuthorized(TeamId teamId) { return teams.contains(teamId); }
    boolean authorizePlayer(PlayerId playerId) { ensureCodeLock("authorize players"); return players.add(Objects.requireNonNull(playerId)); }
    boolean deauthorizePlayer(PlayerId playerId) { ensureCodeLock("deauthorize players"); if (owner.equals(playerId)) throw new IllegalArgumentException("Lock owner cannot be deauthorized"); return players.remove(playerId); }
    boolean authorizeTeam(TeamId teamId) { ensureCodeLock("authorize teams"); return teams.add(Objects.requireNonNull(teamId)); }
    boolean deauthorizeTeam(TeamId teamId) { ensureCodeLock("deauthorize teams"); return teams.remove(teamId); }
    private void ensureCodeLock(String action) { if (type == LockType.KEY_LOCK) throw new IllegalArgumentException("KEY_LOCK does not support " + action); }
}
