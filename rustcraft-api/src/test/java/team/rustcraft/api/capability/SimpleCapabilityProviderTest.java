package team.rustcraft.api.capability;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class SimpleCapabilityProviderTest {
    @Test
    void capabilityRegistrationStoresTypedCapability() {
        SimpleCapabilityProvider provider = new SimpleCapabilityProvider();
        CapabilityKey<TestCapability> key = new CapabilityKey<>("rustcraft:test_capability", TestCapability.class);
        TestCapability capability = new TestCapability();

        provider.registerCapability(key, capability);

        assertTrue(provider.hasCapability(key));
        assertSame(capability, provider.getCapability(key).orElseThrow());
    }

    private static final class TestCapability {
    }
}
