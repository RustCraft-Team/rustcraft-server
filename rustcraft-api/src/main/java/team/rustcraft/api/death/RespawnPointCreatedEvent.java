package team.rustcraft.api.death;

import java.util.Objects;
import team.rustcraft.api.event.Event;

/** Event dispatched after a respawn point is created. */
public record RespawnPointCreatedEvent(RespawnPoint respawnPoint) implements Event {
    public RespawnPointCreatedEvent {
        Objects.requireNonNull(respawnPoint, "respawnPoint");
    }
}
