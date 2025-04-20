package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondCommand;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateBondResource;

public class CreateBondCommandFromResourceAssembler {
    public static CreateBondCommand toCommandFromResource(CreateBondResource resource) {
        return new CreateBondCommand(
                resource.bondName(),
                resource.faceValue(),
                resource.issuePrice(),
                resource.purchasePrice(),
                resource.issueDate(),
                resource.maturityDate(),
                resource.totalPeriods(),
                RateType.valueOf(resource.rateType()),
                resource.rateValue(),
                resource.capitalization() != null ? Capitalization.valueOf(resource.capitalization()) : null,
                Frequency.valueOf(resource.frequency()),
                GraceType.valueOf(resource.graceType()),
                resource.graceCapital(),
                resource.graceInterest(),
                resource.commission(),
                resource.marketRate()
        );
    }
}