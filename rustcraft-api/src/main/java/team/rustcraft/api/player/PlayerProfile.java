package team.rustcraft.api.player;

import java.util.Optional;
import team.rustcraft.api.team.TeamId;

/**
 * Minimal RustCraft player profile state independent of Minecraft integration.
 */
public interface PlayerProfile {
    PlayerId id();

    String name();

    Optional<TeamId> teamId();
}
