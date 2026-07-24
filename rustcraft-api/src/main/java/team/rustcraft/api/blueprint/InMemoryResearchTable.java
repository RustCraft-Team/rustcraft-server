package team.rustcraft.api.blueprint;

import java.util.Objects;
import team.rustcraft.api.inventory.Inventory;
import team.rustcraft.api.inventory.InventoryId;
import team.rustcraft.api.inventory.InventoryService;
import team.rustcraft.api.inventory.ItemId;
import team.rustcraft.api.inventory.ItemStack;
import team.rustcraft.api.inventory.ItemStackId;

/**
 * In-memory research table backed by domain inventory and blueprint services.
 */
public final class InMemoryResearchTable implements ResearchTable {
    private final BlueprintService blueprintService;
    private final InventoryService inventoryService;
    private final ItemId scrapItemId;

    public InMemoryResearchTable(BlueprintService blueprintService, InventoryService inventoryService, ItemId scrapItemId) {
        this.blueprintService = Objects.requireNonNull(blueprintService, "blueprintService");
        this.inventoryService = Objects.requireNonNull(inventoryService, "inventoryService");
        this.scrapItemId = Objects.requireNonNull(scrapItemId, "scrapItemId");
    }

    @Override
    public ItemId scrapItemId() {
        return scrapItemId;
    }

    @Override
    public synchronized Blueprint research(InventoryId sourceInventoryId, ItemStackId researchedItemStackId,
            ItemStackId scrapStackId, BlueprintId blueprintId) {
        Objects.requireNonNull(sourceInventoryId, "sourceInventoryId");
        Objects.requireNonNull(researchedItemStackId, "researchedItemStackId");
        Objects.requireNonNull(scrapStackId, "scrapStackId");
        Blueprint blueprint = blueprintService.findBlueprint(blueprintId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown blueprint: " + blueprintId.value()));
        if (blueprint.unlocked()) {
            throw new IllegalStateException("Blueprint is already unlocked: " + blueprintId.value());
        }

        Inventory inventory = inventoryService.findInventory(sourceInventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown inventory: " + sourceInventoryId.value()));
        ItemStack researchedStack = inventory.findStack(researchedItemStackId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown researched item stack: " + researchedItemStackId.value()));
        ItemStack scrapStack = inventory.findStack(scrapStackId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown scrap stack: " + scrapStackId.value()));
        if (!researchedStack.itemId().equals(blueprint.resultItemId())) {
            throw new IllegalArgumentException("Researched item does not match blueprint result item");
        }
        if (!scrapStack.itemId().equals(scrapItemId)) {
            throw new IllegalArgumentException("Scrap stack must contain scrap");
        }
        if (scrapStack.amount() < blueprint.researchScrapCost()) {
            throw new IllegalArgumentException("Not enough scrap to research blueprint");
        }

        inventoryService.removeItem(sourceInventoryId, researchedItemStackId, 1);
        if (blueprint.researchScrapCost() > 0) {
            inventoryService.removeItem(sourceInventoryId, scrapStackId, blueprint.researchScrapCost());
        }
        return blueprintService.learnBlueprint(blueprintId);
    }
}
