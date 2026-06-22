package team.rustcraft.api.event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Simple in-memory {@link EventBus} implementation that delivers events
 * synchronously on the caller's thread.
 *
 * <p>Listeners are invoked in subscription order. This implementation is small
 * and deterministic, making it suitable as the default API foundation and for
 * tests. It does not perform asynchronous scheduling or Minecraft content
 * registration.</p>
 */
public final class SimpleEventBus implements EventBus {
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new LinkedHashMap<>();

    @Override
    public synchronized <E extends Event> void subscribe(Class<E> eventType, EventListener<? super E> listener) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(listener, "listener");
        listeners.computeIfAbsent(eventType, ignored -> new ArrayList<>()).add(listener);
    }

    @Override
    public <E extends Event> void dispatch(E event) {
        Objects.requireNonNull(event, "event");
        List<EventListener<? extends Event>> matchingListeners = new ArrayList<>();
        synchronized (this) {
            for (Map.Entry<Class<? extends Event>, List<EventListener<? extends Event>>> entry : listeners.entrySet()) {
                if (entry.getKey().isInstance(event)) {
                    matchingListeners.addAll(entry.getValue());
                }
            }
        }
        for (EventListener<? extends Event> listener : matchingListeners) {
            dispatchToListener(listener, event);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Event> void dispatchToListener(EventListener<? extends Event> listener, E event) {
        ((EventListener<E>) listener).onEvent(event);
    }
}
