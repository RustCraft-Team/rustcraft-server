package team.rustcraft.api.survival;

import java.util.List;
import java.util.Objects;
import team.rustcraft.api.inventory.ItemId;

/** Nutritional definition for a domain item that can be consumed as food. */
public record FoodDefinition(ItemId itemId, double nutrition, double healingContribution, List<FoodEffect> effects) {
    public FoodDefinition {
        Objects.requireNonNull(itemId, "itemId");
        if (nutrition < 0) {
            throw new IllegalArgumentException("Nutrition must not be negative");
        }
        if (healingContribution < 0) {
            throw new IllegalArgumentException("Healing contribution must not be negative");
        }
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
    }
}
