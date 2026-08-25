package team.rustcraft.api.status;

import java.time.Instant;

@FunctionalInterface
public interface PeriodicStatusEffect { void apply(ActiveStatusEffect effect, Instant now); }
