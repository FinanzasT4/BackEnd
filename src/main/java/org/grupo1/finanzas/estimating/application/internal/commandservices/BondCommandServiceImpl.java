package org.grupo1.finanzas.estimating.application.internal.commandservices;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteBondByIdCommand;
import org.grupo1.finanzas.estimating.domain.services.BondCommandService;
import org.grupo1.finanzas.estimating.domain.services.EstimationCalculatorService;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.BondRepository;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.ResultRepository;
import org.grupo1.finanzas.iam.domain.model.aggregates.User;
import org.grupo1.finanzas.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BondCommandServiceImpl implements BondCommandService {

    private final BondRepository bondRepository;
    private final ResultRepository resultRepository;
    private final EstimationCalculatorService estimationCalculatorService;

    private final UserRepository userRepository;

    public BondCommandServiceImpl(BondRepository bondRepository, ResultRepository resultRepository, EstimationCalculatorService estimationCalculatorService, UserRepository userRepository) {
        this.bondRepository = bondRepository;
        this.resultRepository = resultRepository;
        this.estimationCalculatorService = estimationCalculatorService;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Bond> handle(CreateBondCommand command) {

        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        Bond bond = new Bond(command, user);
        Bond savedBond = bondRepository.save(bond);

        // Creamos un Result automáticamente para este Bond
        Result result = estimationCalculatorService.calculateFrom(savedBond, new CreateResultCommand(savedBond.getId()));

        if (result == null) {
            throw new RuntimeException("Failed to calculate result for bond: " + savedBond.getId());
        }

        resultRepository.save(result);

        return Optional.of(savedBond);
    }

    @Override
    public void handle(DeleteBondByIdCommand bond) {
        if (!bondRepository.existsById(bond.idBond())) {
            throw new RuntimeException("Result not found with ID: " + bond.idBond());
        }

        bondRepository.deleteById(bond.idBond());
    }
}
