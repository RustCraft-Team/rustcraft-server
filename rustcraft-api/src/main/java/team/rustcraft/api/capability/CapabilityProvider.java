package team.rustcraft.api.capability;

import java.util.Optional;

/**
 * Source of typed optional capabilities.
 */
public interface CapabilityProvider {
    /**
     * Registers a capability implementation with this provider.
     *
     * @param key capability key
     * @param capability capability implementation
     * @param <T> capability contract type
     */
    <T> void registerCapability(CapabilityKey<T> key, T capability);

    /**
     * Looks up a capability by key.
     *
     * @param key capability key
     * @param <T> capability contract type
     * @return matching capability if registered
     */
    <T> Optional<T> getCapability(CapabilityKey<T> key);

    /**
     * Checks whether a capability is registered.
     *
     * @param key capability key
     * @param <T> capability contract type
     * @return {@code true} when a capability is present
     */
    default <T> boolean hasCapability(CapabilityKey<T> key) {
        return getCapability(key).isPresent();
    }
}
