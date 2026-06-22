package team.rustcraft.api.team;

import team.rustcraft.api.capability.CapabilityKey;

/**
 * Capability contract for objects that expose a RustCraft team.
 */
public interface TeamCapability {
    CapabilityKey<TeamCapability> KEY = new CapabilityKey<>("rustcraft:team", TeamCapability.class);

    Team team();
}
