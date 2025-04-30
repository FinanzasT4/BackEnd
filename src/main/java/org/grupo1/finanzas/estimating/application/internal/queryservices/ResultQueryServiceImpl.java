package org.grupo1.finanzas.estimating.application.internal.queryservices;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllResultsQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetResultByBondIdQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetResultByIdQuery;
import org.grupo1.finanzas.estimating.domain.services.ResultQueryService;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.ResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultQueryServiceImpl implements ResultQueryService {

    private final ResultRepository resultRepository;

    public ResultQueryServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public List<Result> handle(GetAllResultsQuery query) {
        return resultRepository.findAll();
    }

    @Override
    public Optional<Result> handle(GetResultByIdQuery query) {
        return resultRepository.findById(query.resultId());
    }

    @Override
    public Optional<Result> handle(GetResultByBondIdQuery query) {
        return resultRepository.findByBondId(query.bondId());
    }
}
