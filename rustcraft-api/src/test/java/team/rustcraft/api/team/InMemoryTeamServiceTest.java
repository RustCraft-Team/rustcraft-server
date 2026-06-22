package team.rustcraft.api.team;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.InMemoryPlayerCapability;
import team.rustcraft.api.player.PlayerCapability;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.player.PlayerProfile;

final class InMemoryTeamServiceTest {
    @Test
    void createsTeamWithLeaderTagsFriendlyFireFlagAndEvents() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryTeamService service = new InMemoryTeamService(3, eventBus);
        PlayerId leader = player(1);

        Team team = service.createTeam(new TeamId("red"), "Red Team", leader, true, Map.of("color", "red"));

        assertEquals(new TeamId("red"), team.id());
        assertEquals(leader, team.leaderId());
        assertEquals(TeamRole.LEADER, team.roleOf(leader));
        assertTrue(team.members().contains(leader));
        assertTrue(team.friendlyFireDisabled());
        assertEquals("red", team.tags().get("color"));
        assertEquals(3, team.maxSize());
        assertEquals(List.of(TeamCreatedEvent.class, TeamMemberJoinedEvent.class), events.stream().map(Event::getClass).toList());
    }

    @Test
    void joinsAndLeavesMembersUntilConfiguredSizeLimit() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryTeamService service = new InMemoryTeamService(2, eventBus);
        TeamId teamId = new TeamId("blue");
        PlayerId leader = player(1);
        PlayerId member = player(2);
        PlayerId rejected = player(3);
        service.createTeam(teamId, "Blue Team", leader, false, Map.of());

        Team joined = service.joinTeam(teamId, member);

        assertEquals(2, joined.members().size());
        assertEquals(TeamRole.MEMBER, joined.roleOf(member));
        assertThrows(IllegalStateException.class, () -> service.joinTeam(teamId, rejected));

        Team left = service.leaveTeam(teamId, member);

        assertFalse(left.members().contains(member));
        assertTrue(events.stream().anyMatch(TeamMemberLeftEvent.class::isInstance));
    }

    @Test
    void disbandsTeamAndPreventsLeaderLeavingDirectly() {
        InMemoryTeamService service = new InMemoryTeamService(2, new SimpleEventBus());
        TeamId teamId = new TeamId("green");
        PlayerId leader = player(1);
        service.createTeam(teamId, "Green Team", leader, false, Map.of());

        assertThrows(IllegalArgumentException.class, () -> service.leaveTeam(teamId, leader));
        assertTrue(service.findTeam(teamId).isPresent());
        assertTrue(service.disbandTeam(teamId));
        assertTrue(service.findTeam(teamId).isEmpty());
        assertFalse(service.disbandTeam(teamId));
    }

    @Test
    void inMemoryCapabilitiesExposeProfilesAndTeams() {
        PlayerId leader = player(1);
        Team team = new InMemoryTeam(new TeamId("yellow"), "Yellow Team", leader, Set.of(leader), Map.of(), false, 2);
        PlayerProfile profile = new InMemoryPlayerProfile(leader, "Leader");

        PlayerCapability playerCapability = new InMemoryPlayerCapability(profile);
        TeamCapability teamCapability = new InMemoryTeamCapability(team);

        assertEquals(profile, playerCapability.profile());
        assertEquals(team, teamCapability.team());
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
