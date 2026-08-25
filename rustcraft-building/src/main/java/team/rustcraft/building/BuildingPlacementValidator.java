package team.rustcraft.building;

import team.rustcraft.api.building.*;
import team.rustcraft.api.death.WorldPosition;
import team.rustcraft.api.player.PlayerId;
import java.util.*;

public final class BuildingPlacementValidator {
    public enum Reason { OK, UNSUPPORTED_PIECE, OCCUPIED, UNSUPPORTED, NO_BUILDING_PRIVILEGE }
    public record Result(boolean valid, Reason reason) { public static Result ok(){return new Result(true, Reason.OK);} public static Result fail(Reason r){return new Result(false,r);} }
    public interface WorldAccess { boolean isAir(WorldPosition p); boolean isSolid(WorldPosition p); }
    private final BuildingService service; private final BuildingConfig config;
    public BuildingPlacementValidator(BuildingService service, BuildingConfig config){this.service=Objects.requireNonNull(service);this.config=Objects.requireNonNull(config);}
    public Result validateFoundation(PlayerId player, WorldPosition origin, WorldAccess world){
        if (!hasPrivilege(player, origin)) return Result.fail(Reason.NO_BUILDING_PRIVILEGE);
        for (WorldPosition p: footprint(origin)) if (!world.isAir(p)) return Result.fail(Reason.OCCUPIED);
        boolean supported=false; for (int x=0;x<4;x++) for(int z=0;z<4;z++) supported |= world.isSolid(new WorldPosition(origin.worldId(), origin.x()+x, origin.y()-1, origin.z()+z));
        return supported ? Result.ok() : Result.fail(Reason.UNSUPPORTED);
    }
    public boolean hasPrivilege(PlayerId player, WorldPosition pos){
        List<ToolCupboard> tcs = service.toolCupboards().stream().filter(ToolCupboard::active).filter(tc -> tc.position().worldId().equals(pos.worldId())).filter(tc -> distSq(tc.position(), pos) <= config.toolCupboardPrivilegeRadius()*config.toolCupboardPrivilegeRadius()).toList();
        return tcs.isEmpty() || tcs.stream().anyMatch(tc -> tc.isPlayerAuthorized(player));
    }
    public static List<WorldPosition> footprint(WorldPosition o){ List<WorldPosition> out=new ArrayList<>(); for(int x=0;x<4;x++) for(int z=0;z<4;z++) out.add(new WorldPosition(o.worldId(),o.x()+x,o.y(),o.z()+z)); return out; }
    private static int distSq(WorldPosition a, WorldPosition b){ int dx=a.x()-b.x(), dy=a.y()-b.y(), dz=a.z()-b.z(); return dx*dx+dy*dy+dz*dz; }
}
