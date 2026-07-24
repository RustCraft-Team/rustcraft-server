package team.rustcraft.api.raid;

/** Stable identifier for a RustCraft raid session. */
public record RaidSessionId(String value) {
    public RaidSessionId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Raid session id must not be blank");
    }
}
