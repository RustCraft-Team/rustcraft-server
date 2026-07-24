package team.rustcraft.api.raid;

/** Stable identifier for a RustCraft door. */
public record DoorId(String value) {
    public DoorId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Door id must not be blank");
    }
}
