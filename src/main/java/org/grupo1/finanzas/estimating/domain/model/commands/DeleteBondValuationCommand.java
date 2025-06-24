package org.grupo1.finanzas.estimating.domain.model.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DeleteBondValuationCommand(
        @NotNull @Positive Long valuationId
) {}
