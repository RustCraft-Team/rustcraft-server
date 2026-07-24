package team.rustcraft.api.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.event.SimpleEventBus;

final class InMemoryItemRegistryTest {
    @Test
    void registersCompleteMinecraftIndependentItemDefinitionAndPublishesEvent() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryItemRegistry registry = new InMemoryItemRegistry(eventBus);
        ItemDefinition hatchet = hatchet();

        ItemDefinition registered = registry.register(hatchet);

        assertEquals(hatchet, registered);
        assertEquals(new ItemDefinitionId("rustcraft:stone_hatchet"), registered.id());
        assertEquals("stone_hatchet", registered.internalName());
        assertEquals("Stone Hatchet", registered.displayName());
        assertEquals(ItemCategory.TOOL, registered.category());
        assertEquals(1, registered.maxStackSize());
        assertTrue(registered.durabilitySupported());
        assertEquals(120, registered.maxDurability());
        assertTrue(registered.tradable());
        assertTrue(registered.researchable());
        assertTrue(registered.craftable());
        assertTrue(registered.repairable());
        assertEquals(ItemRarity.COMMON, registered.defaultRarity());
        assertTrue(registered.tags().contains(new ItemTag("gathering")));
        assertTrue(registered.properties().contains(new ItemProperty("tier", "primitive")));
        assertEquals(List.of(ItemRegisteredEvent.class), events.stream().map(Event::getClass).toList());
    }

    @Test
    void looksUpDefinitionsByIdInternalNameCategoryAndTags() {
        InMemoryItemRegistry registry = new InMemoryItemRegistry(new SimpleEventBus());
        ItemDefinition hatchet = hatchet();
        ItemDefinition sulfur = resource("rustcraft:sulfur_ore", "sulfur_ore", "Sulfur Ore", "ore");
        ItemDefinition stone = resource("rustcraft:stones", "stones", "Stones", "gathering");
        registry.register(hatchet);
        registry.register(sulfur);
        registry.register(stone);

        assertEquals(hatchet, registry.findById(hatchet.id()).orElseThrow());
        assertEquals(sulfur, registry.findByInternalName("sulfur_ore").orElseThrow());
        assertEquals(List.of(sulfur, stone), registry.findByCategory(ItemCategory.RESOURCE));
        assertEquals(List.of(hatchet, stone), registry.findByTag(new ItemTag("gathering")));
        assertEquals(List.of(hatchet), registry.findByTags(List.of(new ItemTag("gathering"), new ItemTag("tool"))));
        assertEquals(List.of(hatchet, sulfur, stone), registry.definitions());
    }

    @Test
    void unregistersDefinitionsAndPublishesEvent() {
        SimpleEventBus eventBus = new SimpleEventBus();
        List<Event> events = new ArrayList<>();
        eventBus.subscribe(Event.class, events::add);
        InMemoryItemRegistry registry = new InMemoryItemRegistry(eventBus);
        ItemDefinition hatchet = registry.register(hatchet());

        assertEquals(hatchet, registry.unregister(hatchet.id()).orElseThrow());

        assertTrue(registry.findById(hatchet.id()).isEmpty());
        assertTrue(registry.findByInternalName(hatchet.internalName()).isEmpty());
        assertTrue(registry.unregister(hatchet.id()).isEmpty());
        assertEquals(List.of(ItemRegisteredEvent.class, ItemUnregisteredEvent.class), events.stream().map(Event::getClass).toList());
    }

    @Test
    void rejectsDuplicateIdAndInternalName() {
        InMemoryItemRegistry registry = new InMemoryItemRegistry(new SimpleEventBus());
        ItemDefinition hatchet = registry.register(hatchet());

        assertThrows(IllegalArgumentException.class, () -> registry.register(hatchet));
        assertThrows(IllegalArgumentException.class, () -> registry.register(new ItemDefinition(
                new ItemDefinitionId("rustcraft:duplicate_hatchet"),
                hatchet.internalName(),
                "Duplicate Hatchet",
                ItemCategory.TOOL,
                1,
                true,
                50,
                true,
                true,
                true,
                true,
                ItemRarity.COMMON,
                Set.of(new ItemTag("tool")),
                Set.of())));
    }

    @Test
    void validatesDefinitionFieldsAndCopiesCollections() {
        Set<ItemTag> tags = new java.util.LinkedHashSet<>();
        tags.add(new ItemTag("resource"));
        ItemDefinition resource = new ItemDefinition(
                new ItemDefinitionId("rustcraft:wood"),
                "wood",
                "Wood",
                ItemCategory.RESOURCE,
                1000,
                false,
                null,
                true,
                true,
                true,
                false,
                ItemRarity.COMMON,
                tags,
                null);
        tags.add(new ItemTag("mutated"));

        assertEquals(Set.of(new ItemTag("resource")), resource.tags());
        assertTrue(resource.properties().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> resource.tags().add(new ItemTag("new")));
        assertThrows(IllegalArgumentException.class, () -> new ItemDefinitionId(" "));
        assertThrows(IllegalArgumentException.class, () -> new ItemTag(" "));
        assertThrows(IllegalArgumentException.class, () -> new ItemProperty(" ", "value"));
        assertThrows(IllegalArgumentException.class, () -> new ItemDefinition(
                new ItemDefinitionId("rustcraft:broken"), "broken", "Broken", ItemCategory.TOOL, 1,
                false, 10, false, false, false, false, ItemRarity.COMMON, Set.of(), Set.of()));
        assertFalse(resource.durabilitySupported());
    }

    private static ItemDefinition hatchet() {
        return new ItemDefinition(
                new ItemDefinitionId("rustcraft:stone_hatchet"),
                "stone_hatchet",
                "Stone Hatchet",
                ItemCategory.TOOL,
                1,
                true,
                120,
                true,
                true,
                true,
                true,
                ItemRarity.COMMON,
                Set.of(new ItemTag("tool"), new ItemTag("gathering")),
                Set.of(new ItemProperty("tier", "primitive")));
    }

    private static ItemDefinition resource(String id, String internalName, String displayName, String extraTag) {
        return new ItemDefinition(
                new ItemDefinitionId(id),
                internalName,
                displayName,
                ItemCategory.RESOURCE,
                1000,
                false,
                null,
                true,
                true,
                true,
                false,
                ItemRarity.COMMON,
                Set.of(new ItemTag("resource"), new ItemTag(extraTag)),
                Set.of());
    }
}
