package team.rustcraft.api.item;

import java.util.List;
import java.util.Optional;

/** Registry for RustCraft-domain item definitions. */
public interface ItemRegistry {
    ItemDefinition register(ItemDefinition definition);

    Optional<ItemDefinition> unregister(ItemDefinitionId id);

    Optional<ItemDefinition> findById(ItemDefinitionId id);

    Optional<ItemDefinition> findByInternalName(String internalName);

    List<ItemDefinition> findByCategory(ItemCategory category);

    List<ItemDefinition> findByTag(ItemTag tag);

    List<ItemDefinition> findByTags(Iterable<ItemTag> tags);

    List<ItemDefinition> definitions();
}
