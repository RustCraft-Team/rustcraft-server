package team.rustcraft.api.event;

/**
 * Marker interface for events published through the RustCraft API event system.
 *
 * <p>Events are plain data objects that describe something that happened inside a
 * RustCraft module. The API foundation intentionally does not define gameplay
 * events; feature modules are expected to declare their own event types by
 * implementing this interface.</p>
 */
public interface Event {
}
