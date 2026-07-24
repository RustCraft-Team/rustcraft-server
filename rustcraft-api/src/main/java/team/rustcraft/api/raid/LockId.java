package team.rustcraft.api.raid;

/** Stable identifier for a RustCraft lock. */
public record LockId(String value) {
    public LockId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Lock id must not be blank");
    }
}
