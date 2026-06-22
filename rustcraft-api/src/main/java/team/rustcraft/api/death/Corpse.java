package team.rustcraft.api.death;

import java.time.Instant;
import team.rustcraft.api.player.PlayerId;

/**
 * Minecraft-independent corpse model created when a player dies.
 */
public interface Corpse {
    CorpseId id();

    PlayerId owner();

    WorldPosition position();

    Instant createdAt();
}
