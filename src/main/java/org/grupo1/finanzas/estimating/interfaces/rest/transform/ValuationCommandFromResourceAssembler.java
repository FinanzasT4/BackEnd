package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.commands.CreateBondValuationCommand;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CreateValuationResource;

public class ValuationCommandFromResourceAssembler {
    public static CreateBondValuationCommand toCommandFromResource(CreateValuationResource resource) {

        // 1. Manejar la capitalización de forma segura
        final Capitalization capitalizationEnum; // Hacemos la variable final

        String capitalizationStr = resource.capitalization();
        if (capitalizationStr != null && !capitalizationStr.isBlank()) {
            // Solo si la cadena no es nula ni vacía, intentamos la conversión
            capitalizationEnum = Capitalization.valueOf(capitalizationStr.toUpperCase());
        } else {
            // En cualquier otro caso (null o vacío), el resultado es null
            capitalizationEnum = null;
        }

        // 2. Construir el comando con el valor seguro
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
                capitalizationEnum,
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