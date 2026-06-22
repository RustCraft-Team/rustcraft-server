package team.rustcraft.api.death;

/**
 * Stable identifier for a RustCraft corpse.
 *
 * @param value non-blank corpse identifier
 */
public record CorpseId(String value) {
    /**
     * Creates a corpse id after validating its value.
     */
    public CorpseId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Corpse id must not be blank");
        }
    }
}
