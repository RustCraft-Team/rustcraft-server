package team.rustcraft.api.event;

/**
 * Handles events delivered by an {@link EventBus}.
 *
 * @param <E> event type handled by this listener
 */
@FunctionalInterface
public interface EventListener<E extends Event> {
    /**
     * Invoked synchronously when an event of the subscribed type is dispatched.
     *
     * @param event event instance being delivered
     */
    void onEvent(E event);
}
