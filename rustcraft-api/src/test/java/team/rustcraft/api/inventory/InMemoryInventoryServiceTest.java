package team.rustcraft.api.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.OwnerRef;

final class InMemoryInventoryServiceTest {
    @Test
    void createsInventoryAndPublishesEvent() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryInventoryService service = new InMemoryInventoryService(eventBus);
        OwnerRef owner = OwnerRef.player(player(1));

        Inventory inventory = service.createInventory(new InventoryId("player-main"), owner, InventoryType.PLAYER, 24);

        assertEquals(new InventoryId("player-main"), inventory.id());
        assertEquals(owner, inventory.owner());
        assertEquals(InventoryType.PLAYER, inventory.type());
        assertEquals(24, inventory.slotCount());
        assertTrue(inventory.stacks().isEmpty());
        assertEquals(List.of(InventoryCreatedEvent.class), events.stream().map(Event::getClass).toList());
    }

    @Test
    void addsAndRemovesDomainItemStacksWithoutMinecraftItems() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryInventoryService service = new InMemoryInventoryService(eventBus);
        InventoryId inventoryId = new InventoryId("chest-1");
        service.createInventory(inventoryId, OwnerRef.player(player(1)), InventoryType.CHEST, 2);
        Item scrap = new InMemoryItem(new ItemId("rustcraft:scrap"), "Scrap", 1000);

        ItemStack stack = service.addItem(inventoryId, scrap, 250);
        ItemStack removed = service.removeItem(inventoryId, stack.id(), 75);

        Inventory inventory = service.findInventory(inventoryId).orElseThrow();
        assertEquals(scrap.id(), stack.itemId());
        assertEquals(250, stack.amount());
        assertEquals(75, removed.amount());
        assertEquals(175, inventory.findStack(stack.id()).orElseThrow().amount());
        assertTrue(events.stream().anyMatch(ItemAddedEvent.class::isInstance));
        assertTrue(events.stream().anyMatch(ItemRemovedEvent.class::isInstance));
    }

    @Test
    void movesSplitsAndMergesStacks() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryInventoryService service = new InMemoryInventoryService(eventBus);
        InventoryId playerInventory = new InventoryId("player");
        InventoryId corpseInventory = new InventoryId("corpse");
        service.createInventory(playerInventory, OwnerRef.player(player(1)), InventoryType.PLAYER, 3);
        service.createInventory(corpseInventory, OwnerRef.player(player(1)), InventoryType.CORPSE, 2);
        Item wood = new InMemoryItem(new ItemId("rustcraft:wood"), "Wood", 1000);
        ItemStack original = service.addItem(playerInventory, wood, 400);

        ItemStack split = service.splitStack(playerInventory, original.id(), 150);
        ItemStack merged = service.mergeStack(playerInventory, split.id(), original.id());
        ItemStack moved = service.moveItem(playerInventory, corpseInventory, original.id(), 200);

        assertEquals(400, merged.amount());
        assertEquals(200, service.findInventory(playerInventory).orElseThrow().findStack(original.id()).orElseThrow().amount());
        assertEquals(200, moved.amount());
        assertFalse(service.findInventory(corpseInventory).orElseThrow().stacks().isEmpty());
        assertEquals(3, events.stream().filter(ItemMovedEvent.class::isInstance).count());
    }

    @Test
    void enforcesSlotsStackLimitsAndCompatibleMerges() {
        InMemoryInventoryService service = new InMemoryInventoryService(new SimpleEventBus());
        InventoryId inventoryId = new InventoryId("vending-machine");
        service.createInventory(inventoryId, OwnerRef.player(player(1)), InventoryType.VENDING_MACHINE, 2);
        Item lowGrade = new InMemoryItem(new ItemId("rustcraft:low_grade"), "Low Grade Fuel", 500);
        Item stone = new InMemoryItem(new ItemId("rustcraft:stone"), "Stone", 1000);

        assertThrows(IllegalArgumentException.class, () -> service.addItem(inventoryId, lowGrade, 501));
        ItemStack fuel = service.addItem(inventoryId, lowGrade, 300);
        ItemStack stoneStack = service.addItem(inventoryId, stone, 300);

        assertThrows(IllegalStateException.class, () -> service.splitStack(inventoryId, fuel.id(), 100));
        assertThrows(IllegalArgumentException.class, () -> service.mergeStack(inventoryId, stoneStack.id(), fuel.id()));
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }
}
