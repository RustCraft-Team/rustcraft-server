package team.rustcraft.api.service;

import java.util.Optional;

/**
 * Registry for typed services shared between RustCraft modules.
 */
public interface ServiceRegistry {
    /**
     * Registers a service instance for the supplied key.
     *
     * @param key service key
     * @param service service instance assignable to the key type
     * @param <T> service contract type
     */
    <T> void register(ServiceKey<T> key, T service);

    /**
     * Looks up a service by key.
     *
     * @param key service key
     * @param <T> service contract type
     * @return matching service if registered
     */
    <T> Optional<T> lookup(ServiceKey<T> key);

    /**
     * Looks up a service or throws when it has not been registered.
     *
     * @param key service key
     * @param <T> service contract type
     * @return registered service
     */
    default <T> T require(ServiceKey<T> key) {
        return lookup(key).orElseThrow(() -> new IllegalStateException("Missing service: " + key.id()));
    }
}
