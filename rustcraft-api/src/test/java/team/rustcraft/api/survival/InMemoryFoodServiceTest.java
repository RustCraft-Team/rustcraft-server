package team.rustcraft.api.survival;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.health.HealthProfile;
import team.rustcraft.api.health.InMemoryHealthService;
import team.rustcraft.api.health.SimpleDamageSource;
import team.rustcraft.api.health.DamageType;
import team.rustcraft.api.inventory.InMemoryInventoryService;
import team.rustcraft.api.inventory.InMemoryItem;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.InventoryType;
import team.rustcraft.api.inventory.Item;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.inventory.ItemRemovedEvent;
import team.rustcraft.api.inventory.ItemStack;
import team.rustcraft.api.player.PlayerId;
import team.rustcraft.api.team.OwnerRef;

final class InMemoryFoodServiceTest {
    private static final Instant NOW = Instant.parse("2026-08-25T00:00:00Z");

    @Test
    void consumesFoodThroughDomainInventoryAndUpdatesHealthOwnedHunger() {
        Fixture fixture = fixture();
        ItemStack stack = fixture.inventory.addItem(fixture.inventoryId, fixture.apple, 3);

        FoodProfile profile = fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 2, NOW.plusSeconds(1));
        HealthProfile health = fixture.health.findProfile(fixture.player).orElseThrow();

        assertEquals(50, profile.hunger());
        assertEquals(50, health.hunger());
        assertEquals(1, fixture.inventory.findInventory(fixture.inventoryId).orElseThrow().findStack(stack.id()).orElseThrow().amount());
        assertTrue(fixture.events.stream().anyMatch(ItemRemovedEvent.class::isInstance));
        assertTrue(fixture.events.stream().anyMatch(FoodConsumedEvent.class::isInstance));
        assertTrue(fixture.events.stream().anyMatch(HungerChangedEvent.class::isInstance));
    }

    @Test
    void foodCanContributeConfigurableHealingWithoutBypassingDeadChecks() {
        Fixture fixture = fixture();
        ItemStack stack = fixture.inventory.addItem(fixture.inventoryId, fixture.apple, 1);
        fixture.health.damage(fixture.player, 25, new SimpleDamageSource(DamageType.FALL, "fall"), NOW);

        fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 1, NOW.plusSeconds(1));

        HealthProfile health = fixture.health.findProfile(fixture.player).orElseThrow();
        assertEquals(80, health.currentHealth());
        assertEquals(25, health.hunger());
    }

    @Test
    void appliesOnlyGenericTemporaryFoodEffectsAndExpiresThem() {
        Fixture fixture = fixture();
        Item berry = new InMemoryItem(new ItemId("rustcraft:berry"), "Berry", 20);
        FoodEffect warmth = new FoodEffect("rustcraft:warmth", Duration.ofSeconds(30), Map.of("amount", 1.5));
        fixture.food.registerFood(new FoodDefinition(berry.id(), 5, 0, List.of(warmth)));
        ItemStack stack = fixture.inventory.addItem(fixture.inventoryId, berry, 1);

        FoodProfile active = fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 1, NOW.plusSeconds(1));
        FoodProfile expired = fixture.food.findProfile(fixture.player, NOW.plusSeconds(32)).orElseThrow();

        assertEquals(1, active.activeEffects().size());
        assertEquals("rustcraft:warmth", active.activeEffects().get(0).key());
        assertTrue(active.activeEffects().get(0).attributes().containsKey("amount"));
        assertTrue(expired.activeEffects().isEmpty());
    }

    @Test
    void hungerStillPreventsNaturalRegenerationAccordingToHealthServiceRules() {
        Fixture fixture = fixture();
        fixture.health.damage(fixture.player, 20, new SimpleDamageSource(DamageType.FALL, "fall"), NOW);

        HealthProfile hungry = fixture.health.tick(fixture.player, NOW.plusSeconds(10));
        ItemStack stack = fixture.inventory.addItem(fixture.inventoryId, fixture.apple, 1);
        fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 1, NOW.plusSeconds(10));
        HealthProfile fed = fixture.health.tick(fixture.player, NOW.plusSeconds(20));

        assertEquals(80, hungry.currentHealth());
        assertTrue(hungry.hungry());
        assertEquals(90, fed.currentHealth());
        assertFalse(fed.hungry());
    }

    @Test
    void rejectsInvalidConsumptionMissingInventoryItemsDeadPlayersAndInvalidQuantities() {
        Fixture fixture = fixture();
        ItemStack stack = fixture.inventory.addItem(fixture.inventoryId, fixture.apple, 1);

        assertThrows(IllegalArgumentException.class, () -> fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 0, NOW));
        assertThrows(IllegalArgumentException.class, () -> fixture.food.consume(fixture.player, fixture.inventoryId, stack.id(), 2, NOW));
        assertThrows(IllegalArgumentException.class, () -> fixture.food.consume(fixture.player, new InventoryId("missing"), stack.id(), 1, NOW));

        PlayerId dead = player(2);
        fixture.health.createProfile(dead, 100, 0, NOW);
        fixture.food.createProfile(dead, 100, NOW);
        fixture.health.damage(dead, 100, new SimpleDamageSource(DamageType.MELEE, "rock"), NOW);
        assertThrows(IllegalStateException.class, () -> fixture.food.consume(dead, fixture.inventoryId, stack.id(), 1, NOW));

        Item rock = new InMemoryItem(new ItemId("rustcraft:rock"), "Rock", 1);
        ItemStack rockStack = fixture.inventory.addItem(fixture.inventoryId, rock, 1);
        assertThrows(IllegalArgumentException.class, () -> fixture.food.consume(fixture.player, fixture.inventoryId, rockStack.id(), 1, NOW));
    }

    private static Fixture fixture() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryHealthService health = new InMemoryHealthService(eventBus);
        InMemoryInventoryService inventory = new InMemoryInventoryService(eventBus);
        InMemoryFoodService food = new InMemoryFoodService(health, inventory, eventBus);
        PlayerId player = player(1);
        InventoryId inventoryId = new InventoryId("player-food");
        Item apple = new InMemoryItem(new ItemId("rustcraft:apple"), "Apple", 10);
        health.createProfile(player, 100, 0, NOW);
        food.createProfile(player, 100, NOW);
        inventory.createInventory(inventoryId, OwnerRef.player(player), InventoryType.PLAYER, 4);
        food.registerFood(new FoodDefinition(apple.id(), 25, 5, List.of()));
        return new Fixture(eventBus, events, health, inventory, food, player, inventoryId, apple);
    }

    private static PlayerId player(int id) {
        return new PlayerId(new UUID(0L, id));
    }

    private record Fixture(SimpleEventBus eventBus, List<Event> events, InMemoryHealthService health, InMemoryInventoryService inventory, InMemoryFoodService food, PlayerId player, InventoryId inventoryId, Item apple) {}
}
