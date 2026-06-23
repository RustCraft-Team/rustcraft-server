package team.rustcraft.api.building;

/** Stable identifier for a RustCraft tool cupboard. */
public record ToolCupboardId(String value) {
    public ToolCupboardId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tool cupboard id must not be blank");
        }
    }
}
