package team.rustcraft.api.death;

import java.util.Objects;
import team.rustcraft.api.event.Event;
import team.rustcraft.api.player.PlayerId;

/** Event dispatched after a player respawns at a respawn point. */
public record PlayerRespawnedEvent(PlayerId playerId, RespawnPoint respawnPoint) implements Event {
    public PlayerRespawnedEvent {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(respawnPoint, "respawnPoint");
    }
}
