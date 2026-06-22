package team.rustcraft.api.service;

import java.util.Objects;

/**
 * Typed identifier for a service exposed through a {@link ServiceRegistry}.
 *
 * @param id stable service identifier, typically namespaced by module id
 * @param type Java type implemented by the service instance
 * @param <T> service contract type
 */
public record ServiceKey<T>(String id, Class<T> type) {
    /**
     * Creates a service key after validating its identifier and service type.
     */
    public ServiceKey {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Service id must not be blank");
        }
        Objects.requireNonNull(type, "type");
    }
}
