package team.rustcraft.api.team;

/**
 * Stable identifier for a RustCraft team.
 *
 * @param value non-blank team identifier
 */
public record TeamId(String value) {
    /**
     * Creates a team id after validating its value.
     */
    public TeamId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Team id must not be blank");
        }
    }
}
