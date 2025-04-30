package org.grupo1.finanzas.estimating.domain.services;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteBondByIdCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.UpdateBondCommand;

import java.util.Optional;

public interface BondCommandService {
    Optional<Bond> handle(CreateBondCommand command);
    Optional<Bond> handle(UpdateBondCommand command);
    void handle(DeleteBondByIdCommand result);
}
