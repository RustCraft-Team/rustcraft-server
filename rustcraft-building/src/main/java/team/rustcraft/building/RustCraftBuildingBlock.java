package team.rustcraft.building;
import net.minecraft.block.Block;
import team.rustcraft.api.building.BuildingGrade;
public class RustCraftBuildingBlock extends Block { public final BuildingGrade grade; public RustCraftBuildingBlock(Settings settings, BuildingGrade grade){ super(settings); this.grade=grade; } }
