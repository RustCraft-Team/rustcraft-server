package team.rustcraft.api.blueprint;

import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.inventory.ItemStackId;

/**
 * Domain API for Rust-style research table operations.
 */
public interface ResearchTable {
    ItemId scrapItemId();

    Blueprint research(
            InventoryId sourceInventoryId,
            ItemStackId researchedItemStackId,
            ItemStackId scrapStackId,
            BlueprintId blueprintId
    );
}
