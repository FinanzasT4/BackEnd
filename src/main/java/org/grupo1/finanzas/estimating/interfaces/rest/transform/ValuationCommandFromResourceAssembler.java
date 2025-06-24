package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateValuationResource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValuationCommandFromResourceAssembler {
    /**
     * Convierte un CreateValuationResource (de la API) en un CreateBondValuationCommand (para la aplicación).
     */
    public CreateBondValuationCommand toCommandFromResource(CreateValuationResource resource) {
        return new CreateBondValuationCommand(
                resource.valuationName(),
                resource.userId(),
                resource.faceValue(),
                resource.issuePrice(),
                resource.purchasePrice(),
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
                resource.commission(),
                resource.marketRate()
        );
    }
}
