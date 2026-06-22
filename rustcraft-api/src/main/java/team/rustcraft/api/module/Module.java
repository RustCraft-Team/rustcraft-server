package team.rustcraft.api.module;

import team.rustcraft.api.event.EventBus;
import team.rustcraft.api.service.ServiceRegistry;

/**
 * Public contract implemented by RustCraft modules.
 */
public interface Module {
    /**
     * @return stable module identifier
     */
    String id();

    /**
     * Initializes the module's API-level integrations.
     *
     * @param services shared service registry
     * @param events shared event bus
     */
    void initialize(ServiceRegistry services, EventBus events);

    /**
     * @return current module lifecycle phase
     */
    default ModuleLifecyclePhase lifecyclePhase() {
        return ModuleLifecyclePhase.CREATED;
    }
}
