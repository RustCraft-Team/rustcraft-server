package team.rustcraft.building;

import team.rustcraft.api.building.BuildingGrade;
import java.util.Map;

public interface UpgradeCostProvider {
    Map<String, Integer> costFor(BuildingGrade from, BuildingGrade to);
    UpgradeCostProvider FREE = (from, to) -> Map.of();
}
