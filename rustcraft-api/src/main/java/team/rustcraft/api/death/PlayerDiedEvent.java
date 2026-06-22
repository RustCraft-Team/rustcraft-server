package team.rustcraft.api.death;

import java.util.Objects;
import team.rustcraft.api.event.Event;

/** Event dispatched after a player's corpse is created. */
public record PlayerDiedEvent(Corpse corpse) implements Event {
    public PlayerDiedEvent {
        Objects.requireNonNull(corpse, "corpse");
    }
}
