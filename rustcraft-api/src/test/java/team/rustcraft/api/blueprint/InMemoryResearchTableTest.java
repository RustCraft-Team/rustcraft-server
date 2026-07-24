package team.rustcraft.api.blueprint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.inventory.InMemoryInventoryService;
import team.rustcraft.api.inventory.InMemoryItem;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.InventoryType;
import team.rustcraft.api.inventory.Item;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.inventory.ItemStack;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.OwnerRef;

final class InMemoryResearchTableTest {
    @Test
    void researchConsumesOneResearchedItemAndScrapThenLearnsBlueprint() {
        InMemoryBlueprintService blueprintService = new InMemoryBlueprintService();
        InMemoryInventoryService inventoryService = new InMemoryInventoryService(new SimpleEventBus());
        OwnerRef owner = OwnerRef.player(player(1));
        InventoryId inventoryId = new InventoryId("player-main");
        inventoryService.createInventory(inventoryId, owner, InventoryType.PLAYER, 4);
        ItemId scrapId = new ItemId("rustcraft:scrap");
        ItemId pistolId = new ItemId("rustcraft:semi_auto_pistol");
        Item pistol = new InMemoryItem(pistolId, "Semi-Automatic Pistol", 1);
        Item scrap = new InMemoryItem(scrapId, "Scrap", 1000);
        ItemStack pistolStack = inventoryService.addItem(inventoryId, pistol, 1);
        ItemStack scrapStack = inventoryService.addItem(inventoryId, scrap, 250);
        BlueprintId blueprintId = new BlueprintId("player-1:pistol");
        blueprintService.createLockedBlueprint(blueprintId, owner, pistolId, WorkbenchTier.TIER_1, 125);
        ResearchTable researchTable = new InMemoryResearchTable(blueprintService, inventoryService, scrapId);

        Blueprint researched = researchTable.research(inventoryId, pistolStack.id(), scrapStack.id(), blueprintId);

        assertEquals(BlueprintState.LEARNED, researched.state());
        assertFalse(inventoryService.findInventory(inventoryId).orElseThrow().findStack(pistolStack.id()).isPresent());
        assertEquals(125, inventoryService.findInventory(inventoryId).orElseThrow().findStack(scrapStack.id()).orElseThrow().amount());
    }

    @Test
    void researchRequiresMatchingItemAndEnoughScrap() {
        InMemoryBlueprintService blueprintService = new InMemoryBlueprintService();
        InMemoryInventoryService inventoryService = new InMemoryInventoryService(new SimpleEventBus());
        OwnerRef owner = OwnerRef.player(player(1));
        InventoryId inventoryId = new InventoryId("player-main");
        inventoryService.createInventory(inventoryId, owner, InventoryType.PLAYER, 4);
        ItemId scrapId = new ItemId("rustcraft:scrap");
        ItemStack stonePickaxe = inventoryService.addItem(inventoryId,
                new InMemoryItem(new ItemId("rustcraft:stone_pickaxe"), "Stone Pickaxe", 1), 1);
        ItemStack scrapStack = inventoryService.addItem(inventoryId, new InMemoryItem(scrapId, "Scrap", 1000), 25);
        BlueprintId blueprintId = new BlueprintId("player-1:metal-pickaxe");
        blueprintService.createLockedBlueprint(
                blueprintId,
                owner,
                new ItemId("rustcraft:metal_pickaxe"),
                WorkbenchTier.TIER_1,
                75
        );
        ResearchTable researchTable = new InMemoryResearchTable(blueprintService, inventoryService, scrapId);

        assertThrows(IllegalArgumentException.class,
                () -> researchTable.research(inventoryId, stonePickaxe.id(), scrapStack.id(), blueprintId));
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
