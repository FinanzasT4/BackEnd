package org.grupo1.finanzas.estimating.application.internal.commandservices;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteResultByIdCommand;
import org.grupo1.finanzas.estimating.domain.services.EstimationCalculatorService;
import org.grupo1.finanzas.estimating.domain.services.ResultCommandService;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.BondRepository;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.ResultRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResultCommandServiceImpl implements ResultCommandService {

    private final BondRepository bondRepository;
    private final ResultRepository resultRepository;
    private final EstimationCalculatorService estimationCalculatorService;

    public ResultCommandServiceImpl(BondRepository bondRepository, ResultRepository resultRepository, EstimationCalculatorService estimationCalculatorService) {
        this.bondRepository = bondRepository;
        this.resultRepository = resultRepository;
        this.estimationCalculatorService = estimationCalculatorService;
    }

    @Override
    public Optional<Result> handle(CreateResultCommand command) {
        Bond bond = bondRepository.findById(command.bondId())
                .orElseThrow(() -> new RuntimeException("Bond not found"));

        Result result = estimationCalculatorService.calculateFrom(bond, command);
        return Optional.of(resultRepository.save(result));
    }

    @Override
    public void handle(DeleteResultByIdCommand result) {
        if (!resultRepository.existsById(result.resultId())) {
            throw new RuntimeException("Result not found with ID: " + result.resultId());
        }

        Result resultToDelete = resultRepository.findById(result.resultId())
                .orElseThrow(() -> new RuntimeException("Result not found with ID: " + result));

        if (!bondRepository.existsById(resultToDelete.getBondId())) {
            throw new RuntimeException("Bond associated not found with ID: " + resultToDelete.getBondId());
        }

        resultRepository.deleteById(result.resultId());
        bondRepository.deleteById(resultToDelete.getBondId());
    }
}
