package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.health.HealthProfile;
import team.rustcraft.api.health.HealthService;
import team.rustcraft.api.inventory.Inventory;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.InventoryService;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.inventory.ItemStack;
import team.rustcraft.api.inventory.ItemStackId;
import team.rustcraft.api.player.PlayerId;

/** Simple in-memory {@link FoodService}; delegates inventory and hunger ownership to existing domain services. */
public final class InMemoryFoodService implements FoodService {
    private final Map<ItemId, FoodDefinition> foods = new LinkedHashMap<>();
    private final Map<PlayerId, Double> maxHunger = new LinkedHashMap<>();
    private final Map<PlayerId, List<ActiveFoodEffect>> effects = new LinkedHashMap<>();
    private final HealthService healthService;
    private final InventoryService inventoryService;
    private final EventBus eventBus;

    public InMemoryFoodService(HealthService healthService, InventoryService inventoryService, EventBus eventBus) {
        this.healthService = Objects.requireNonNull(healthService, "healthService");
        this.inventoryService = Objects.requireNonNull(inventoryService, "inventoryService");
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized FoodDefinition registerFood(FoodDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        foods.put(definition.itemId(), definition);
        return definition;
    }

    @Override
    public synchronized Optional<FoodDefinition> findFood(ItemId itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return Optional.ofNullable(foods.get(itemId));
    }

    @Override
    public synchronized FoodProfile createProfile(PlayerId playerId, double maxHungerValue, Instant now) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(now, "now");
        if (maxHungerValue <= 0) {
            throw new IllegalArgumentException("Max hunger must be positive");
        }
        if (maxHunger.containsKey(playerId)) {
            throw new IllegalArgumentException("Food profile already exists: " + playerId.value());
        }
        HealthProfile health = healthService.findProfile(playerId).orElseThrow(() -> new IllegalArgumentException("Unknown health profile: " + playerId.value()));
        maxHunger.put(playerId, maxHungerValue);
        effects.put(playerId, List.of());
        return profile(playerId, health.hunger(), now);
    }

    @Override
    public synchronized Optional<FoodProfile> findProfile(PlayerId playerId, Instant now) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(now, "now");
        if (!maxHunger.containsKey(playerId)) {
            return Optional.empty();
        }
        HealthProfile health = healthService.findProfile(playerId).orElseThrow(() -> new IllegalArgumentException("Unknown health profile: " + playerId.value()));
        pruneEffects(playerId, now);
        return Optional.of(profile(playerId, health.hunger(), now));
    }

    @Override
    public synchronized FoodProfile consume(PlayerId playerId, InventoryId inventoryId, ItemStackId stackId, int quantity, Instant now) {
        validateQuantity(quantity);
        Objects.requireNonNull(inventoryId, "inventoryId");
        Objects.requireNonNull(stackId, "stackId");
        HealthProfile health = healthService.findProfile(playerId).orElseThrow(() -> new IllegalArgumentException("Unknown health profile: " + playerId.value()));
        if (!health.alive()) {
            throw new IllegalStateException("Dead players cannot consume food");
        }
        if (!maxHunger.containsKey(playerId)) {
            throw new IllegalArgumentException("Unknown food profile: " + playerId.value());
        }
        Inventory inventory = inventoryService.findInventory(inventoryId).orElseThrow(() -> new IllegalArgumentException("Unknown inventory: " + inventoryId.value()));
        ItemStack stack = inventory.findStack(stackId).orElseThrow(() -> new IllegalArgumentException("Unknown stack: " + stackId.value()));
        if (quantity > stack.amount()) {
            throw new IllegalArgumentException("Cannot consume more items than the stack contains");
        }
        FoodDefinition food = foods.get(stack.itemId());
        if (food == null) {
            throw new IllegalArgumentException("Item is not registered as food: " + stack.itemId().value());
        }

        inventoryService.removeItem(inventoryId, stackId, quantity);
        double previousHunger = health.hunger();
        double nutrition = food.nutrition() * quantity;
        HealthProfile fed = healthService.setHunger(playerId, Math.min(maxHunger.get(playerId), previousHunger + nutrition), now);
        double healing = food.healingContribution() * quantity;
        if (healing > 0) {
            fed = healthService.heal(playerId, healing, now);
        }
        addEffects(playerId, food, quantity, now);
        FoodProfile profile = profile(playerId, fed.hunger(), now);
        if (Double.compare(previousHunger, fed.hunger()) != 0) {
            eventBus.dispatch(new HungerChangedEvent(playerId, previousHunger, fed.hunger(), profile, now));
        }
        eventBus.dispatch(new FoodConsumedEvent(playerId, inventoryId, stack.itemId(), quantity, nutrition, healing, profile, now));
        return profile;
    }

    private void addEffects(PlayerId playerId, FoodDefinition food, int quantity, Instant now) {
        pruneEffects(playerId, now);
        List<ActiveFoodEffect> active = new ArrayList<>(effects.getOrDefault(playerId, List.of()));
        for (int i = 0; i < quantity; i++) {
            for (FoodEffect effect : food.effects()) {
                active.add(new ActiveFoodEffect(effect.key(), now.plus(effect.duration()), effect.attributes()));
            }
        }
        effects.put(playerId, List.copyOf(active));
    }

    private FoodProfile profile(PlayerId playerId, double hunger, Instant now) {
        pruneEffects(playerId, now);
        return new FoodProfile(playerId, hunger, maxHunger.get(playerId), effects.getOrDefault(playerId, List.of()), now);
    }

    private void pruneEffects(PlayerId playerId, Instant now) {
        effects.put(playerId, effects.getOrDefault(playerId, List.of()).stream().filter(effect -> effect.activeAt(now)).toList());
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }
}
