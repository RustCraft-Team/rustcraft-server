package team.rustcraft.api.item;

import java.util.Set;

/**
 * RustCraft item definition model.
 *
 * <p>This type intentionally contains only RustCraft domain data and does not
 * reference Minecraft, Fabric, recipes, blocks, entities, GUIs, or gameplay mechanics.</p>
 */
public record ItemDefinition(
        ItemDefinitionId id,
        String internalName,
        String displayName,
        ItemCategory category,
        int maxStackSize,
        boolean durabilitySupported,
        Integer maxDurability,
        boolean tradable,
        boolean researchable,
        boolean craftable,
        boolean repairable,
        ItemRarity defaultRarity,
        Set<ItemTag> tags,
        Set<ItemProperty> properties
) {
    public ItemDefinition {
        if (id == null) {
            throw new IllegalArgumentException("Item definition id must not be null");
        }
        if (internalName == null || internalName.isBlank()) {
            throw new IllegalArgumentException("Item internal name must not be blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Item display name must not be blank");
        }
        if (category == null) {
            throw new IllegalArgumentException("Item category must not be null");
        }
        if (maxStackSize < 1) {
            throw new IllegalArgumentException("Max stack size must be at least 1");
        }
        if (maxDurability != null && maxDurability < 1) {
            throw new IllegalArgumentException("Max durability must be at least 1 when provided");
        }
        if (!durabilitySupported && maxDurability != null) {
            throw new IllegalArgumentException("Max durability requires durability support");
        }
        if (defaultRarity == null) {
            throw new IllegalArgumentException("Default rarity must not be null");
        }
        tags = Set.copyOf(tags == null ? Set.of() : tags);
        properties = Set.copyOf(properties == null ? Set.of() : properties);
    }
}
