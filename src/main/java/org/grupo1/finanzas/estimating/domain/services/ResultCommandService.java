package org.grupo1.finanzas.estimating.domain.services;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteResultByIdCommand;

import java.util.Optional;

public interface ResultCommandService {
    Optional<Result> handle(CreateResultCommand command);

    void handle(DeleteResultByIdCommand resultId);
}
