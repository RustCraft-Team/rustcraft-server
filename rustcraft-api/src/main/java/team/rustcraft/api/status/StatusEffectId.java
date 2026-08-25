package team.rustcraft.api.status;

import java.util.Objects;

public record StatusEffectId(String value) { public StatusEffectId { Objects.requireNonNull(value, "value"); if (value.isBlank()) throw new IllegalArgumentException("Status effect id cannot be blank"); } }
