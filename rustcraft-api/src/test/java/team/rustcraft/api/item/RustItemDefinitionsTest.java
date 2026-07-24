package team.rustcraft.api.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import team.rustcraft.api.event.SimpleEventBus;

final class RustItemDefinitionsTest {
    @Test
    void registersExpectedCatalogSize() {
        InMemoryItemRegistry registry = new InMemoryItemRegistry(new SimpleEventBus());

        RustItemDefinitions.registerAll(registry);

        assertEquals(RustItemDefinitions.DEFINITION_COUNT, registry.definitions().size());
        assertTrue(registry.definitions().size() >= 240);
    }

    @Test
    void catalogHasNoDuplicateIdsOrInternalNames() {
        Set<ItemDefinitionId> ids = new HashSet<>();
        Set<String> internalNames = new HashSet<>();

        for (ItemDefinition definition : RustItemDefinitions.DEFINITIONS) {
            assertTrue(ids.add(definition.id()), () -> "Duplicate id: " + definition.id().value());
            assertTrue(internalNames.add(definition.internalName()), () -> "Duplicate internal name: " + definition.internalName());
        }
        assertDoesNotThrow(() -> RustItemDefinitions.registerAll(new InMemoryItemRegistry(new SimpleEventBus())));
    }

    @Test
    void findsDefinitionsByCategory() {
        InMemoryItemRegistry registry = RustItemDefinitions.createRegistry(new SimpleEventBus());

        assertFalse(registry.findByCategory(ItemCategory.RESOURCE).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.COMPONENT).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.MEDICAL).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.FOOD).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.AMMUNITION).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.WEAPON).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.ARMOR).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.TOOL).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.BUILDING).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.DOOR).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.LOCK).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.CONTAINER).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.DEPLOYABLE).isEmpty());
        assertFalse(registry.findByCategory(ItemCategory.VEHICLE).isEmpty());
    }

    @Test
    void findsDefinitionsByTags() {
        InMemoryItemRegistry registry = RustItemDefinitions.createRegistry(new SimpleEventBus());

        assertFalse(registry.findByTag(ItemTag.RESOURCE).isEmpty());
        assertFalse(registry.findByTag(ItemTag.WEAPON).isEmpty());
        assertFalse(registry.findByTag(ItemTag.MELEE).isEmpty());
        assertFalse(registry.findByTag(ItemTag.RANGED).isEmpty());
        assertFalse(registry.findByTag(ItemTag.AMMO).isEmpty());
        assertFalse(registry.findByTag(ItemTag.MEDICAL).isEmpty());
        assertFalse(registry.findByTag(ItemTag.FOOD).isEmpty());
        assertFalse(registry.findByTag(ItemTag.ARMOR).isEmpty());
        assertFalse(registry.findByTag(ItemTag.BUILDING).isEmpty());
        assertFalse(registry.findByTag(ItemTag.DOOR).isEmpty());
        assertFalse(registry.findByTag(ItemTag.LOCK).isEmpty());
        assertFalse(registry.findByTag(ItemTag.CONTAINER).isEmpty());
        assertFalse(registry.findByTag(ItemTag.DEPLOYABLE).isEmpty());
        assertFalse(registry.findByTag(ItemTag.CRAFTING).isEmpty());
        assertFalse(registry.findByTag(ItemTag.WORKBENCH).isEmpty());
        assertFalse(registry.findByTag(ItemTag.SCRAP_ITEM).isEmpty());
        assertFalse(registry.findByTag(ItemTag.FUEL).isEmpty());
        assertFalse(registry.findByTag(ItemTag.VEHICLE).isEmpty());
        assertFalse(registry.findByTag(ItemTag.RAID).isEmpty());
        assertFalse(registry.findByTag(ItemTag.TOOL).isEmpty());
        assertFalse(registry.findByTags(List.of(ItemTag.WEAPON, ItemTag.RANGED)).isEmpty());
    }

    @Test
    void definitionsHaveValidRequiredFieldsAndStacks() {
        for (ItemDefinition definition : RustItemDefinitions.DEFINITIONS) {
            assertNotNull(definition.id());
            assertFalse(definition.id().value().isBlank());
            assertFalse(definition.internalName().isBlank());
            assertFalse(definition.displayName().isBlank());
            assertNotNull(definition.category());
            assertTrue(definition.maxStackSize() >= 1, definition.internalName());
            assertTrue(definition.maxStackSize() <= 1000, definition.internalName());
            assertNotNull(definition.defaultRarity());
            assertFalse(definition.tags().isEmpty(), definition.internalName());
            if (definition.durabilitySupported()) {
                assertNotNull(definition.maxDurability(), definition.internalName());
                assertTrue(definition.maxDurability() > 0, definition.internalName());
            } else {
                assertEquals(null, definition.maxDurability(), definition.internalName());
                assertFalse(definition.repairable(), definition.internalName());
            }
        }
    }
}
