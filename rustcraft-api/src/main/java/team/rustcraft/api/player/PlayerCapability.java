package team.rustcraft.api.player;

import team.rustcraft.api.capability.CapabilityKey;

/**
 * Capability contract for objects that expose a RustCraft player profile.
 */
public interface PlayerCapability {
    CapabilityKey<PlayerCapability> KEY = new CapabilityKey<>("rustcraft:player", PlayerCapability.class);

    PlayerProfile profile();
}
