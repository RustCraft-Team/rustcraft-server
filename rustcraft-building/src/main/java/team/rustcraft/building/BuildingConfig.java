package team.rustcraft.building;

public record BuildingConfig(boolean enabled, int toolCupboardPrivilegeRadius) {
    public static BuildingConfig defaults() { return new BuildingConfig(true, 20); }
}
