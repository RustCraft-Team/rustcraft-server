package team.rustcraft.api.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class SimpleServiceRegistryTest {
    @Test
    void serviceRegistrationStoresTypedService() {
        SimpleServiceRegistry registry = new SimpleServiceRegistry();
        ServiceKey<TestService> key = new ServiceKey<>("rustcraft:test", TestService.class);
        TestService service = new TestService();

        registry.register(key, service);

        assertSame(service, registry.require(key));
    }

    @Test
    void serviceLookupReturnsRegisteredService() {
        SimpleServiceRegistry registry = new SimpleServiceRegistry();
        ServiceKey<TestService> key = new ServiceKey<>("rustcraft:test", TestService.class);
        TestService service = new TestService();

        registry.register(key, service);

        assertTrue(registry.lookup(key).isPresent());
        assertSame(service, registry.lookup(key).orElseThrow());
    }

    private static final class TestService {
    }
}
