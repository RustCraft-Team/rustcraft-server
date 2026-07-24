package team.rustcraft.api.blueprint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.OwnerRef;

final class InMemoryBlueprintServiceTest {
    @Test
    void storesPlayerOwnedBlueprintsIndependentlyFromInventories() {
        InMemoryBlueprintService service = new InMemoryBlueprintService();
        OwnerRef owner = OwnerRef.player(player(1));
        BlueprintId blueprintId = new BlueprintId("player-1:semi-rifle");

        Blueprint blueprint = service.createLockedBlueprint(
                blueprintId,
                owner,
                new ItemId("rustcraft:semi_auto_rifle"),
                WorkbenchTier.TIER_2,
                500
        );

        assertEquals(owner, blueprint.owner());
        assertEquals(BlueprintState.LOCKED, blueprint.state());
        assertFalse(blueprint.unlocked());
        assertTrue(service.findBlueprint(blueprintId).isPresent());
    }

    @Test
    void supportsDefaultUnlockedLearnedLockedAndFragmentBlueprints() {
        InMemoryBlueprintService service = new InMemoryBlueprintService();
        OwnerRef owner = OwnerRef.player(player(1));
        BlueprintId spear = new BlueprintId("player-1:wooden-spear");
        BlueprintId garageDoor = new BlueprintId("player-1:garage-door");

        Blueprint defaultUnlocked = service.createDefaultUnlockedBlueprint(
                spear,
                owner,
                new ItemId("rustcraft:wooden_spear"),
                WorkbenchTier.NONE
        );
        Blueprint fragmentBlueprint = service.createFragmentBlueprint(
                garageDoor,
                owner,
                new ItemId("rustcraft:garage_door"),
                WorkbenchTier.TIER_2,
                75,
                new ItemId("rustcraft:garage_door_blueprint_fragment"),
                20
        );

        assertEquals(BlueprintState.DEFAULT_UNLOCKED, defaultUnlocked.state());
        assertTrue(defaultUnlocked.unlocked());
        assertEquals(BlueprintState.LOCKED, fragmentBlueprint.state());
        assertEquals(new ItemId("rustcraft:garage_door_blueprint_fragment"), fragmentBlueprint.fragmentItemId().orElseThrow());
        assertEquals(20, fragmentBlueprint.requiredFragmentCount());
        assertEquals(BlueprintState.LEARNED, service.learnBlueprint(garageDoor).state());
        assertEquals(BlueprintState.LOCKED, service.lockBlueprint(garageDoor).state());
    }

    @Test
    void researchedRecipesStillRequireTheirWorkbenchTier() {
        InMemoryBlueprintService service = new InMemoryBlueprintService();
        BlueprintId rocketLauncher = new BlueprintId("player-1:rocket-launcher");
        service.createLockedBlueprint(
                rocketLauncher,
                OwnerRef.player(player(1)),
                new ItemId("rustcraft:rocket_launcher"),
                WorkbenchTier.TIER_3,
                750
        );

        service.learnBlueprint(rocketLauncher);

        assertFalse(service.canCraft(rocketLauncher, WorkbenchTier.NONE));
        assertFalse(service.canCraft(rocketLauncher, WorkbenchTier.TIER_2));
        assertTrue(service.canCraft(rocketLauncher, WorkbenchTier.TIER_3));
    }

    @Test
    void rejectsDuplicateBlueprints() {
        InMemoryBlueprintService service = new InMemoryBlueprintService();
        BlueprintId id = new BlueprintId("player-1:hatchet");
        OwnerRef owner = OwnerRef.player(player(1));
        service.createLockedBlueprint(id, owner, new ItemId("rustcraft:hatchet"), WorkbenchTier.TIER_1, 75);

        assertThrows(IllegalArgumentException.class,
                () -> service.createLockedBlueprint(id, owner, new ItemId("rustcraft:hatchet"), WorkbenchTier.TIER_1, 75));
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
