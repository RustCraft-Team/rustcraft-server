package team.rustcraft.api.survival;

import java.time.Instant;
import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.player.PlayerId;

/** Event published after a player consumes a domain food item. */
public record FoodConsumedEvent(PlayerId playerId, InventoryId inventoryId, ItemId itemId, int quantity, double nutrition, double healingContribution, FoodProfile profile, Instant occurredAt) implements Event {
    public FoodConsumedEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(inventoryId, "inventoryId");
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
