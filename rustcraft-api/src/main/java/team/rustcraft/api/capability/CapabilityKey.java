package team.rustcraft.api.capability;

import java.util.Objects;

/**
 * Typed identifier for an optional capability provided by a RustCraft module or object.
 *
 * @param id stable capability identifier, typically namespaced by module id
 * @param type Java type implemented by the capability instance
 * @param <T> capability contract type
 */
public record CapabilityKey<T>(String id, Class<T> type) {
    /**
     * Creates a capability key after validating its identifier and type.
     */
    public CapabilityKey {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Capability id must not be blank");
        }
        Objects.requireNonNull(type, "type");
    }
}
