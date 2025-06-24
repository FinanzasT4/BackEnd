package org.grupo1.finanzas.estimating.domain.services.commandservices;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.DeleteBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.commands.UpdateBondValuationCommand;

import java.util.Optional;

/**
 * Contrato para los casos de uso de escritura del aggregate BondValuation.
 */
public interface BondValuationCommandService {
    Optional<Long> handle(CreateBondValuationCommand command);
    Optional<Long> handle(UpdateBondValuationCommand command);
    void handle(DeleteBondValuationCommand command);
}