package team.rustcraft.api.death;

import java.util.Objects;
import team.rustcraft.api.event.Event;

/** Event dispatched after a respawn point is destroyed. */
public record RespawnPointDestroyedEvent(RespawnPoint respawnPoint) implements Event {
    public RespawnPointDestroyedEvent {
        Objects.requireNonNull(respawnPoint, "respawnPoint");
    }
}
