package team.rustcraft.api.team;

import java.util.Map;
import java.util.Set;
import team.rustcraft.api.player.PlayerId;

/**
 * Team domain model independent of commands, gameplay, and Minecraft integration.
 */
public interface Team {
    TeamId id();

    String name();

    PlayerId leaderId();

    Set<PlayerId> members();

    Map<String, String> tags();

    boolean friendlyFireDisabled();

    int maxSize();

    default boolean contains(PlayerId playerId) {
        return members().contains(playerId);
    }

    default TeamRole roleOf(PlayerId playerId) {
        return leaderId().equals(playerId) ? TeamRole.LEADER : TeamRole.MEMBER;
    }
}
