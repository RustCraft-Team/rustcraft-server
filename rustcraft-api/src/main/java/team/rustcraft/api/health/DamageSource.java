package team.rustcraft.api.health;

import java.util.Optional;
import team.rustcraft.api.player.PlayerId;

/** Platform-independent description of where damage came from. */
public interface DamageSource {
    DamageType type();
    Optional<PlayerId> attacker();
    String description();
}
