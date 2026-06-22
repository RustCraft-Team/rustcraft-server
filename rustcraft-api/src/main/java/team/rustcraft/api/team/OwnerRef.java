package team.rustcraft.api.team;

import java.util.Objects;
import team.rustcraft.api.player.PlayerId;

/**
 * Reference to the player or team that owns a RustCraft object.
 *
 * @param type owner type
 * @param id serialized owner id
 */
public record OwnerRef(OwnerType type, String id) {
    /**
     * Supported owner reference types.
     */
    public enum OwnerType {
        PLAYER,
        TEAM
    }

    /**
     * Creates an owner reference after validating its fields.
     */
    public OwnerRef {
        Objects.requireNonNull(type, "type");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Owner id must not be blank");
        }
    }

    public static OwnerRef player(PlayerId playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return new OwnerRef(OwnerType.PLAYER, playerId.value().toString());
    }

    public static OwnerRef team(TeamId teamId) {
        Objects.requireNonNull(teamId, "teamId");
        return new OwnerRef(OwnerType.TEAM, teamId.value());
    }
}
