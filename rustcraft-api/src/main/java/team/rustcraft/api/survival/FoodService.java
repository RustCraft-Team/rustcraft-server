package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.Optional;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.ItemStackId;
import team.rustcraft.api.player.PlayerId;

/** Domain-only service for food consumption, nutrition, hunger and food effects. */
public interface FoodService {
    FoodDefinition registerFood(FoodDefinition definition);

    Optional<FoodDefinition> findFood(team.rustcraft.api.inventory.ItemId itemId);

    FoodProfile createProfile(PlayerId playerId, double maxHunger, Instant now);

    Optional<FoodProfile> findProfile(PlayerId playerId, Instant now);

    FoodProfile consume(PlayerId playerId, InventoryId inventoryId, ItemStackId stackId, int quantity, Instant now);
}
