package org.grupo1.finanzas.estimating.domain.services;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface ResultQueryService {
    List<Result> handle(GetAllResultsQuery query);
    Optional<Result> handle(GetResultByIdQuery query);

    Optional<Result> handle(GetResultByBondIdQuery query);
}