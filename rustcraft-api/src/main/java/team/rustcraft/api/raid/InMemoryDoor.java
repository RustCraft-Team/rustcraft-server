package team.rustcraft.api.raid;

import java.util.Objects;
import java.util.Optional;
import team.rustcraft.api.building.BuildingId;
import team.rustcraft.api.player.PlayerId;

/** In-memory {@link Door}. */
public final class InMemoryDoor implements Door {
    private final DoorId id; private final PlayerId owner; private final BuildingId buildingId; private final DoorType type; private final int maxHealth;
    private int currentHealth; private boolean open; private LockId lockId;
    public InMemoryDoor(DoorId id, PlayerId owner, BuildingId buildingId, DoorType type, int maxHealth) {
        if (maxHealth <= 0) throw new IllegalArgumentException("Max health must be positive");
        this.id = Objects.requireNonNull(id); this.owner = Objects.requireNonNull(owner); this.buildingId = Objects.requireNonNull(buildingId); this.type = Objects.requireNonNull(type); this.maxHealth = maxHealth; this.currentHealth = maxHealth;
    }
    @Override public DoorId id() { return id; }
    @Override public PlayerId owner() { return owner; }
    @Override public BuildingId buildingId() { return buildingId; }
    @Override public DoorType type() { return type; }
    @Override public int currentHealth() { return currentHealth; }
    @Override public int maxHealth() { return maxHealth; }
    @Override public boolean isOpen() { return open; }
    @Override public Optional<LockId> lockId() { return Optional.ofNullable(lockId); }
    void setOpen(boolean open) { this.open = open; }
    void attachLock(LockId lockId) { this.lockId = Objects.requireNonNull(lockId); }
    LockId removeLock() { LockId removed = lockId; lockId = null; return removed; }
    int applyDamage(int amount) { if (amount < 0) throw new IllegalArgumentException("Damage amount must not be negative"); int applied = Math.min(currentHealth, amount); currentHealth -= applied; return applied; }
}
