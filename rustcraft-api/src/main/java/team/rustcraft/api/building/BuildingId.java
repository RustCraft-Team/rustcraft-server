package team.rustcraft.api.building;

/** Stable identifier for a RustCraft building. */
public record BuildingId(String value) {
    public BuildingId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Building id must not be blank");
        }
    }
}
