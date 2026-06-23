package team.rustcraft.api.building;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.TeamId;

final class InMemoryBuildingServiceTest {
    @Test
    void createsBuildingsAndBlocksWithoutMinecraftOrFabricTypes() {
        InMemoryBuildingService service = new InMemoryBuildingService(new SimpleEventBus());
        PlayerId owner = player(1);
        BuildingId buildingId = new BuildingId("base-1");
        WorldPosition position = new WorldPosition("minecraft:overworld", 10, 64, 10);

        Building building = service.createBuilding(buildingId, owner);
        BuildingBlock block = service.addBlock(new BuildingBlockId("foundation-1"), buildingId, BuildingBlockType.FOUNDATION, BuildingGrade.STONE, 500, position);

        assertEquals(buildingId, building.id());
        assertEquals(owner, building.owner());
        assertEquals(BuildingGrade.STONE, block.grade());
        assertEquals(500, block.maxHealth());
        assertEquals(500, block.currentHealth());
        assertEquals(owner, block.owner());
        assertEquals(buildingId, block.buildingId());
        assertEquals(position, block.position());
        assertEquals(List.of(block.id()), service.findBuilding(buildingId).orElseThrow().blocks().stream().map(BuildingBlock::id).toList());
    }

    @Test
    void destroysBuildingsAndRemovesTheirBlocks() {
        InMemoryBuildingService service = new InMemoryBuildingService(new SimpleEventBus());
        BuildingId buildingId = new BuildingId("base-2");
        BuildingBlockId blockId = new BuildingBlockId("wall-1");
        service.createBuilding(buildingId, player(1));
        service.addBlock(blockId, buildingId, BuildingBlockType.WALL, BuildingGrade.WOOD, 250, new WorldPosition("minecraft:overworld", 0, 64, 0));

        assertTrue(service.destroyBuilding(buildingId));

        assertTrue(service.findBuilding(buildingId).isEmpty());
        assertTrue(service.findBlock(blockId).isEmpty());
        assertFalse(service.destroyBuilding(buildingId));
    }

    @Test
    void supportsMultipleToolCupboardsWithRustStyleAuthorization() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryBuildingService service = new InMemoryBuildingService(eventBus);
        PlayerId owner = player(1);
        PlayerId friend = player(2);
        TeamId teamId = new TeamId("raiders");
        ToolCupboardId firstId = new ToolCupboardId("tc-1");
        ToolCupboardId secondId = new ToolCupboardId("tc-2");

        ToolCupboard first = service.placeToolCupboard(firstId, owner, new WorldPosition("minecraft:overworld", 1, 64, 1), true);
        ToolCupboard second = service.placeToolCupboard(secondId, friend, new WorldPosition("minecraft:overworld", 100, 64, 100), true);
        service.authorizePlayer(firstId, friend);
        service.authorizeTeam(firstId, teamId);

        assertEquals(2, service.toolCupboards().size());
        assertTrue(first.isPlayerAuthorized(owner));
        assertTrue(first.isPlayerAuthorized(friend));
        assertTrue(first.isTeamAuthorized(teamId));
        assertFalse(second.isPlayerAuthorized(owner));
        assertEquals(List.of(ToolCupboardPlacedEvent.class, ToolCupboardPlacedEvent.class, PlayerAuthorizedEvent.class), events.stream().map(Event::getClass).toList());
    }

    @Test
    void deauthorizesPlayersAndTeamsButKeepsOwnerAuthorized() {
        InMemoryBuildingService service = new InMemoryBuildingService(new SimpleEventBus());
        PlayerId owner = player(1);
        PlayerId friend = player(2);
        TeamId teamId = new TeamId("builders");
        ToolCupboardId id = new ToolCupboardId("tc-3");
        service.placeToolCupboard(id, owner, new WorldPosition("minecraft:overworld", 5, 64, 5), true);
        service.authorizePlayer(id, friend);
        service.authorizeTeam(id, teamId);

        ToolCupboard toolCupboard = service.deauthorizePlayer(id, friend);
        service.deauthorizeTeam(id, teamId);

        assertFalse(toolCupboard.isPlayerAuthorized(friend));
        assertFalse(toolCupboard.isTeamAuthorized(teamId));
        assertThrows(IllegalArgumentException.class, () -> service.deauthorizePlayer(id, owner));
        assertTrue(toolCupboard.isPlayerAuthorized(owner));
    }

    @Test
    void publishesLifecycleEvents() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryBuildingService service = new InMemoryBuildingService(eventBus);
        BuildingId buildingId = new BuildingId("base-3");
        ToolCupboardId toolCupboardId = new ToolCupboardId("tc-4");
        service.createBuilding(buildingId, player(1));
        service.destroyBuilding(buildingId);
        service.placeToolCupboard(toolCupboardId, player(1), new WorldPosition("minecraft:overworld", 0, 64, 0), true);
        service.destroyToolCupboard(toolCupboardId);

        assertEquals(List.of(BuildingCreatedEvent.class, BuildingDestroyedEvent.class, ToolCupboardPlacedEvent.class, ToolCupboardDestroyedEvent.class), events.stream().map(Event::getClass).toList());
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
