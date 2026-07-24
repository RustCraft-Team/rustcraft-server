package team.rustcraft.api.item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.event.EventBus;

/** Synchronous in-memory {@link ItemRegistry} implementation for tests and local usage. */
public final class InMemoryItemRegistry implements ItemRegistry {
    private final Map<ItemDefinitionId, ItemDefinition> definitionsById = new LinkedHashMap<>();
    private final Map<String, ItemDefinitionId> idsByInternalName = new LinkedHashMap<>();
    private final EventBus eventBus;

    public InMemoryItemRegistry(EventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public synchronized ItemDefinition register(ItemDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (definitionsById.containsKey(definition.id())) {
            throw new IllegalArgumentException("Item definition id already registered: " + definition.id().value());
        }
        ItemDefinitionId existingId = idsByInternalName.get(definition.internalName());
        if (existingId != null) {
            throw new IllegalArgumentException("Item internal name already registered: " + definition.internalName());
        }
        definitionsById.put(definition.id(), definition);
        idsByInternalName.put(definition.internalName(), definition.id());
        eventBus.dispatch(new ItemRegisteredEvent(definition));
        return definition;
    }

    @Override
    public synchronized Optional<ItemDefinition> unregister(ItemDefinitionId id) {
        Objects.requireNonNull(id, "id");
        ItemDefinition removed = definitionsById.remove(id);
        if (removed == null) {
            return Optional.empty();
        }
        idsByInternalName.remove(removed.internalName());
        eventBus.dispatch(new ItemUnregisteredEvent(removed));
        return Optional.of(removed);
    }

    @Override
    public synchronized Optional<ItemDefinition> findById(ItemDefinitionId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(definitionsById.get(id));
    }

    @Override
    public synchronized Optional<ItemDefinition> findByInternalName(String internalName) {
        if (internalName == null || internalName.isBlank()) {
            throw new IllegalArgumentException("Item internal name must not be blank");
        }
        ItemDefinitionId id = idsByInternalName.get(internalName);
        return id == null ? Optional.empty() : Optional.of(definitionsById.get(id));
    }

    @Override
    public synchronized List<ItemDefinition> findByCategory(ItemCategory category) {
        Objects.requireNonNull(category, "category");
        return definitionsById.values().stream()
                .filter(definition -> definition.category() == category)
                .toList();
    }

    @Override
    public synchronized List<ItemDefinition> findByTag(ItemTag tag) {
        Objects.requireNonNull(tag, "tag");
        return definitionsById.values().stream()
                .filter(definition -> definition.tags().contains(tag))
                .toList();
    }

    @Override
    public synchronized List<ItemDefinition> findByTags(Iterable<ItemTag> tags) {
        Objects.requireNonNull(tags, "tags");
        List<ItemTag> requiredTags = new ArrayList<>();
        tags.forEach(tag -> requiredTags.add(Objects.requireNonNull(tag, "tag")));
        return definitionsById.values().stream()
                .filter(definition -> definition.tags().containsAll(requiredTags))
                .toList();
    }

    @Override
    public synchronized List<ItemDefinition> definitions() {
        return List.copyOf(definitionsById.values());
    }
}
