package team.rustcraft.api.building;

/** Stable identifier for a RustCraft building block. */
public record BuildingBlockId(String value) {
    public BuildingBlockId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Building block id must not be blank");
        }
    }
}
