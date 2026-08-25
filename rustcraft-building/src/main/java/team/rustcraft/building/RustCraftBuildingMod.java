package team.rustcraft.building;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.rustcraft.api.building.*;
import team.rustcraft.api.event.SimpleEventBus;

public final class RustCraftBuildingMod implements ModInitializer {
    public static final String MOD_ID = "rustcraft-building";
    public static final BuildingConfig CONFIG = BuildingConfig.defaults();
    public static final InMemoryBuildingService BUILDINGS = new InMemoryBuildingService(new SimpleEventBus());
    public static final RustCraftWorldBuildingIndex INDEX = new RustCraftWorldBuildingIndex();
    public static final Block TWIG_FOUNDATION = new RustCraftBuildingBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(1.0f), BuildingGrade.TWIG);
    public static final Block WOOD_FOUNDATION = new RustCraftBuildingBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0f), BuildingGrade.WOOD);
    public static final Block STONE_FOUNDATION = new RustCraftBuildingBlock(FabricBlockSettings.copyOf(Blocks.STONE).strength(4.0f), BuildingGrade.STONE);
    public static final Block METAL_FOUNDATION = new RustCraftBuildingBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(5.0f), BuildingGrade.METAL);
    public static final Block ARMORED_FOUNDATION = new RustCraftBuildingBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK).strength(6.0f), BuildingGrade.ARMORED);
    public static final Block TOOL_CUPBOARD = new ToolCupboardBlock(FabricBlockSettings.copyOf(Blocks.BARREL).strength(2.5f));
    public static final Item BUILDING_PLAN = new BuildingPlanItem(new FabricItemSettings().maxCount(1));
    @Override public void onInitialize() {
        regBlock("twig_foundation", TWIG_FOUNDATION); regBlock("wood_foundation", WOOD_FOUNDATION); regBlock("stone_foundation", STONE_FOUNDATION); regBlock("metal_foundation", METAL_FOUNDATION); regBlock("armored_foundation", ARMORED_FOUNDATION); regBlock("tool_cupboard", TOOL_CUPBOARD);
        Registry.register(Registries.ITEM, id("building_plan"), BUILDING_PLAN);
    }
    private static void regBlock(String name, Block block){ Registry.register(Registries.BLOCK, id(name), block); Registry.register(Registries.ITEM, id(name), new BlockItem(block, new FabricItemSettings())); }
    public static Identifier id(String path){ return new Identifier(MOD_ID, path); }
    public static int maxHealth(BuildingGrade grade){ return switch(grade){ case TWIG -> 10; case WOOD -> 250; case STONE -> 500; case METAL -> 1000; case ARMORED -> 2000; }; }
}
