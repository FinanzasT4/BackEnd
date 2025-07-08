package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateValuationResource;

public class ValuationCommandFromResourceAssembler {
    public static CreateBondValuationCommand toCommandFromResource(CreateValuationResource resource) {
        return new CreateBondValuationCommand(
                resource.valuationName(),
                resource.userId(),
                resource.faceValue(),
                resource.marketPrice(),
                resource.issueDate(),
                resource.maturityDate(),
                resource.totalPeriods(),
                RateType.valueOf(resource.rateType().toUpperCase()),
                resource.rateValue(),
                resource.capitalization() != null ? Capitalization.valueOf(resource.capitalization().toUpperCase()) : null,
                Frequency.valueOf(resource.frequency().toUpperCase()),
                GraceType.valueOf(resource.graceType().toUpperCase()),
                resource.graceCapital(),
                resource.graceInterest(),
                resource.marketRate(),
                resource.issuerStructuringCost(),
                resource.issuerPlacementCost(),
                resource.issuerCavaliCost(),
                resource.investorSabCost(),
                resource.investorCavaliCost()
        );
    }
}