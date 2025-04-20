package org.grupo1.finanzas.estimating.domain.services;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;

public interface EstimationCalculatorService {
    Result calculateFrom(Bond bond, CreateResultCommand command);
}