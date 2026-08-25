package team.rustcraft.building;

import team.rustcraft.api.building.BuildingBlockType;

public enum BuildingPiece {
    FOUNDATION(BuildingBlockType.FOUNDATION, 4, 1, 4);
    public final BuildingBlockType apiType; public final int widthX; public final int height; public final int widthZ;
    BuildingPiece(BuildingBlockType apiType, int widthX, int height, int widthZ) { this.apiType=apiType; this.widthX=widthX; this.height=height; this.widthZ=widthZ; }
}
