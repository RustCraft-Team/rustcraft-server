package team.rustcraft.api.player;

import java.util.Objects;

/**
 * Simple player capability implementation backed by an immutable player profile.
 */
public record InMemoryPlayerCapability(PlayerProfile profile) implements PlayerCapability {
    public InMemoryPlayerCapability {
        Objects.requireNonNull(profile, "profile");
    }
}
