package team.rustcraft.building;

import team.rustcraft.api.building.*;
import team.rustcraft.api.player.PlayerId;
import java.util.*;

public final class BuildingUpgradeService {
 public enum Reason { OK, NOT_FOUND, NOT_ALLOWED, INVALID_TRANSITION, INSUFFICIENT_RESOURCES }
 public record Result(boolean valid, Reason reason, Map<String,Integer> cost){ static Result ok(Map<String,Integer> c){return new Result(true,Reason.OK,c);} static Result fail(Reason r){return new Result(false,r,Map.of());}}
 public interface ResourceAccess { boolean consume(PlayerId player, Map<String,Integer> cost); }
 private final BuildingService service; private final BuildingPlacementValidator privilege; private final UpgradeCostProvider costs;
 public BuildingUpgradeService(BuildingService service, BuildingPlacementValidator privilege, UpgradeCostProvider costs){this.service=service;this.privilege=privilege;this.costs=costs;}
 public Result validateAndConsume(PlayerId player, BuildingBlockId id, BuildingGrade to, ResourceAccess resources){
  Optional<BuildingBlock> block=service.findBlock(id); if(block.isEmpty()) return Result.fail(Reason.NOT_FOUND);
  if(!privilege.hasPrivilege(player, block.get().position())) return Result.fail(Reason.NOT_ALLOWED);
  BuildingGrade from=block.get().grade(); if(from != BuildingGrade.TWIG || to == BuildingGrade.TWIG) return Result.fail(Reason.INVALID_TRANSITION);
  Map<String,Integer> cost=costs.costFor(from,to); return resources.consume(player,cost) ? Result.ok(cost) : Result.fail(Reason.INSUFFICIENT_RESOURCES);
 }
}
