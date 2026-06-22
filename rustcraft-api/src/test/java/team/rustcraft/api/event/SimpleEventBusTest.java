package team.rustcraft.api.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class SimpleEventBusTest {
    @Test
    void eventSubscriptionReceivesMatchingDispatch() {
        SimpleEventBus bus = new SimpleEventBus();
        List<String> received = new ArrayList<>();

        bus.subscribe(TestEvent.class, event -> received.add(event.message()));
        bus.dispatch(new TestEvent("foundation"));

        assertEquals(List.of("foundation"), received);
    }

    @Test
    void eventDispatchUsesSubscriptionOrder() {
        SimpleEventBus bus = new SimpleEventBus();
        List<Integer> order = new ArrayList<>();

        bus.subscribe(TestEvent.class, event -> order.add(1));
        bus.subscribe(TestEvent.class, event -> order.add(2));
        bus.dispatch(new TestEvent("ordered"));

        assertEquals(List.of(1, 2), order);
    }

    private record TestEvent(String message) implements Event {
    }
}
