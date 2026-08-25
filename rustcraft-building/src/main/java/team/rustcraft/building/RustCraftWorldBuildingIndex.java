package team.rustcraft.building;
import net.minecraft.util.math.BlockPos;
import team.rustcraft.api.building.*;
import java.util.*;
public final class RustCraftWorldBuildingIndex { private final Map<String, BuildingBlockId> blocks=new HashMap<>(); private final Map<String, ToolCupboardId> cupboards=new HashMap<>(); public void putBlock(String world, BlockPos pos, BuildingBlockId id){blocks.put(key(world,pos),id);} public Optional<BuildingBlockId> blockAt(String world, BlockPos pos){return Optional.ofNullable(blocks.get(key(world,pos)));} public void putToolCupboard(String world, BlockPos pos, ToolCupboardId id){cupboards.put(key(world,pos),id);} public Optional<ToolCupboardId> toolCupboardAt(String world, BlockPos pos){return Optional.ofNullable(cupboards.get(key(world,pos)));} private static String key(String w, BlockPos p){return w+":"+p.getX()+","+p.getY()+","+p.getZ();}}
