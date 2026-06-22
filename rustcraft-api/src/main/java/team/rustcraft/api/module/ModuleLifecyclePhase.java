package team.rustcraft.api.module;

/**
 * Lifecycle phases used to describe a RustCraft module's initialization state.
 */
public enum ModuleLifecyclePhase {
    /** Module instance exists but has not started initialization. */
    CREATED,
    /** Module is registering API services, events, and capabilities. */
    INITIALIZING,
    /** Module is ready for other modules to consume its API contracts. */
    READY,
    /** Module has been stopped or disabled. */
    STOPPED
}
