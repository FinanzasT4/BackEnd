package org.grupo1.finanzas.estimating.domain.model.commands;

public record CreateResultCommand(Long bondId) {
    public CreateResultCommand {
        if (bondId == null || bondId <= 0)
            throw new IllegalArgumentException("Bond ID must be positive");
    }
}
