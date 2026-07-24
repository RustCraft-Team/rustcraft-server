package team.rustcraft.api.crafting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;
import team.rustcraft.api.item.ItemDefinitionId;

final class InMemoryCraftingServiceTest {
    @Test
    void registersCompleteMinecraftIndependentRecipeDefinition() {
        InMemoryCraftingService service = new InMemoryCraftingService(new SimpleEventBus());
        CraftingRecipe recipe = workbenchRecipe();

        CraftingRecipe registered = service.registerRecipe(recipe);

        assertEquals(recipe, registered);
        assertEquals(new RecipeId("rustcraft:workbench_level_2"), registered.id());
        assertEquals(RecipeCategory.DEPLOYABLE, registered.category());
        assertEquals(new ResearchRequirement(250), registered.researchRequirement());
        assertEquals(WorkbenchTier.TIER1, registered.workbenchTierRequirement());
        assertEquals(Duration.ofSeconds(30), registered.craftingTime());
        assertEquals(List.of(new RecipeIngredient(item("rustcraft:scrap"), 500), new RecipeIngredient(item("rustcraft:metal_fragments"), 500)), registered.ingredients());
        assertEquals(List.of(new RecipeResult(item("rustcraft:workbench_level_2"), 1), new RecipeResult(item("rustcraft:workbench_part"), 2)), registered.outputs());
        assertEquals(List.of(new RecipeResult(item("rustcraft:empty_can"), 1)), registered.byproducts());
        assertTrue(registered.blueprintRequired());
        assertTrue(registered.unlocked());
        assertEquals(new RepairCostDefinition(List.of(new RecipeIngredient(item("rustcraft:metal_fragments"), 125)), 25), registered.repairCostDefinition());
    }

    @Test
    void findsRecipesByIdCategoryAndRegistrationOrder() {
        InMemoryCraftingService service = new InMemoryCraftingService(new SimpleEventBus());
        CraftingRecipe workbench = service.registerRecipe(workbenchRecipe());
        CraftingRecipe bandage = service.registerRecipe(simpleRecipe("rustcraft:bandage", RecipeCategory.CONSUMABLE));

        assertEquals(workbench, service.findRecipe(workbench.id()).orElseThrow());
        assertEquals(List.of(workbench, bandage), service.recipes());
        assertEquals(List.of(bandage), service.findRecipesByCategory(RecipeCategory.CONSUMABLE));
        assertTrue(service.findRecipe(new RecipeId("rustcraft:missing")).isEmpty());
    }

    @Test
    void startsCompletesAndCancelsCraftingWithSynchronousEvents() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryCraftingService service = new InMemoryCraftingService(eventBus);
        CraftingRecipe recipe = service.registerRecipe(workbenchRecipe());
        Instant now = Instant.parse("2026-07-24T12:00:00Z");

        CraftingJob first = service.startCrafting(recipe.id(), 2, WorkbenchTier.TIER2, true, true, now);
        CraftingJob second = service.startCrafting(recipe.id(), 1, WorkbenchTier.TIER1, true, true, now);
        CraftingJob completed = service.completeCrafting(first.id());
        CraftingJob cancelled = service.cancelCrafting(second.id());

        assertEquals(CraftingJobStatus.COMPLETED, completed.status());
        assertEquals(now.plusSeconds(60), completed.completesAt());
        assertEquals(CraftingJobStatus.CANCELLED, cancelled.status());
        assertEquals(List.of(), service.queue().jobs());
        assertEquals(List.of(CraftingStartedEvent.class, CraftingStartedEvent.class, CraftingCompletedEvent.class, CraftingCancelledEvent.class), events.stream().map(Event::getClass).toList());
        assertEquals(completed, ((CraftingCompletedEvent) events.get(2)).job());
    }

    @Test
    void rejectsUnmetValidationRequirements() {
        InMemoryCraftingService service = new InMemoryCraftingService(new SimpleEventBus());
        CraftingRecipe recipe = service.registerRecipe(workbenchRecipe());
        Instant now = Instant.parse("2026-07-24T12:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> service.registerRecipe(recipe));
        assertThrows(IllegalArgumentException.class, () -> service.startCrafting(new RecipeId("rustcraft:missing"), 1, WorkbenchTier.TIER3, true, true, now));
        assertThrows(IllegalArgumentException.class, () -> service.startCrafting(recipe.id(), 0, WorkbenchTier.TIER3, true, true, now));
        assertThrows(IllegalArgumentException.class, () -> service.startCrafting(recipe.id(), 1, WorkbenchTier.NONE, true, true, now));
        assertThrows(IllegalArgumentException.class, () -> service.startCrafting(recipe.id(), 1, WorkbenchTier.TIER1, false, true, now));
        assertThrows(IllegalArgumentException.class, () -> service.startCrafting(recipe.id(), 1, WorkbenchTier.TIER1, true, false, now));
        CraftingRecipe locked = service.registerRecipe(lockedRecipe());
        assertThrows(IllegalStateException.class, () -> service.startCrafting(locked.id(), 1, WorkbenchTier.TIER3, true, true, now));
    }

    @Test
    void validatesDomainRecordsAndCopiesCollections() {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeIngredient(item("rustcraft:cloth"), 15));
        CraftingRecipe recipe = new CraftingRecipe(new RecipeId("rustcraft:bandage"), "Bandage", RecipeCategory.CONSUMABLE, null, WorkbenchTier.NONE,
                Duration.ZERO, ingredients, List.of(new RecipeResult(item("rustcraft:bandage"), 1)), null, false, true, null);
        ingredients.add(new RecipeIngredient(item("rustcraft:mutated"), 1));

        assertEquals(List.of(new RecipeIngredient(item("rustcraft:cloth"), 15)), recipe.ingredients());
        assertFalse(recipe.researchRequirement().required());
        assertFalse(recipe.repairCostDefinition().required());
        assertThrows(UnsupportedOperationException.class, () -> recipe.ingredients().add(new RecipeIngredient(item("rustcraft:new"), 1)));
        assertThrows(IllegalArgumentException.class, () -> new RecipeId(" "));
        assertThrows(IllegalArgumentException.class, () -> new RecipeIngredient(item("rustcraft:cloth"), 0));
        assertThrows(IllegalArgumentException.class, () -> new RecipeResult(item("rustcraft:bandage"), 0));
        assertThrows(IllegalArgumentException.class, () -> new ResearchRequirement(-1));
        assertThrows(IllegalArgumentException.class, () -> new RepairCostDefinition(List.of(), -1));
        assertThrows(IllegalArgumentException.class, () -> new CraftingRecipe(new RecipeId("rustcraft:broken"), "Broken", RecipeCategory.MISC, ResearchRequirement.NONE, WorkbenchTier.NONE, Duration.ZERO, List.of(), List.of(), List.of(), false, true, RepairCostDefinition.NONE));
    }

    private static CraftingRecipe workbenchRecipe() {
        return new CraftingRecipe(
                new RecipeId("rustcraft:workbench_level_2"),
                "Workbench Level 2",
                RecipeCategory.DEPLOYABLE,
                new ResearchRequirement(250),
                WorkbenchTier.TIER1,
                Duration.ofSeconds(30),
                List.of(new RecipeIngredient(item("rustcraft:scrap"), 500), new RecipeIngredient(item("rustcraft:metal_fragments"), 500)),
                List.of(new RecipeResult(item("rustcraft:workbench_level_2"), 1), new RecipeResult(item("rustcraft:workbench_part"), 2)),
                List.of(new RecipeResult(item("rustcraft:empty_can"), 1)),
                true,
                true,
                new RepairCostDefinition(List.of(new RecipeIngredient(item("rustcraft:metal_fragments"), 125)), 25));
    }

    private static CraftingRecipe simpleRecipe(String id, RecipeCategory category) {
        return new CraftingRecipe(new RecipeId(id), id, category, ResearchRequirement.NONE, WorkbenchTier.NONE, Duration.ofSeconds(1), List.of(new RecipeIngredient(item("rustcraft:cloth"), 1)), List.of(new RecipeResult(item(id), 1)), List.of(), false, true, RepairCostDefinition.NONE);
    }

    private static CraftingRecipe lockedRecipe() {
        return new CraftingRecipe(new RecipeId("rustcraft:locked"), "Locked", RecipeCategory.MISC, ResearchRequirement.NONE, WorkbenchTier.NONE, Duration.ZERO, List.of(), List.of(new RecipeResult(item("rustcraft:locked"), 1)), List.of(), false, false, RepairCostDefinition.NONE);
    }

    private static ItemDefinitionId item(String id) {
        return new ItemDefinitionId(id);
    }
}
