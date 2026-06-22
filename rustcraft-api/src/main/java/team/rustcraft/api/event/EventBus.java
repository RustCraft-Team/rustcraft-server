package team.rustcraft.api.event;

/**
 * Synchronous publish/subscribe bus for RustCraft API events.
 *
 * <p>Implementations should deliver an event to listeners registered for the
 * event's concrete class and for any registered parent event types that are
 * assignable from the dispatched event.</p>
 */
public interface EventBus {
    /**
     * Subscribes a listener to events of the supplied type.
     *
     * @param eventType concrete or parent event type to observe
     * @param listener listener invoked when matching events are dispatched
     * @param <E> event type
     */
    <E extends Event> void subscribe(Class<E> eventType, EventListener<? super E> listener);

    /**
     * Dispatches an event immediately on the calling thread.
     *
     * @param event event to deliver
     * @param <E> event type
     */
    <E extends Event> void dispatch(E event);
}
