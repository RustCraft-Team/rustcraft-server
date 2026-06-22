package team.rustcraft.api.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple in-memory {@link ServiceRegistry} implementation.
 */
public final class SimpleServiceRegistry implements ServiceRegistry {
    private final Map<ServiceKey<?>, Object> services = new LinkedHashMap<>();

    @Override
    public synchronized <T> void register(ServiceKey<T> key, T service) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(service, "service");
        if (!key.type().isInstance(service)) {
            throw new IllegalArgumentException("Service " + key.id() + " must implement " + key.type().getName());
        }
        services.put(key, service);
    }

    @Override
    public synchronized <T> Optional<T> lookup(ServiceKey<T> key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(services.get(key)).map(key.type()::cast);
    }
}
