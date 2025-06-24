package org.grupo1.finanzas.estimating.domain.model.queries;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GetValuationByIdQuery(@NotNull @Positive Long valuationId) {}

