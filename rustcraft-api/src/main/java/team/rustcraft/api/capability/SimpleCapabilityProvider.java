package team.rustcraft.api.capability;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple in-memory {@link CapabilityProvider} implementation.
 */
public final class SimpleCapabilityProvider implements CapabilityProvider {
    private final Map<CapabilityKey<?>, Object> capabilities = new LinkedHashMap<>();

    @Override
    public synchronized <T> void registerCapability(CapabilityKey<T> key, T capability) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(capability, "capability");
        if (!key.type().isInstance(capability)) {
            throw new IllegalArgumentException("Capability " + key.id() + " must implement " + key.type().getName());
        }
        capabilities.put(key, capability);
    }

    @Override
    public synchronized <T> Optional<T> getCapability(CapabilityKey<T> key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(capabilities.get(key)).map(key.type()::cast);
    }
}
