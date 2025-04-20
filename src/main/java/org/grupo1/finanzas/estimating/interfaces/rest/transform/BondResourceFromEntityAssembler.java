package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.BondResource;

public class BondResourceFromEntityAssembler {
    public static BondResource toResourceFromEntity(Bond bond) {
        return new BondResource(
                bond.getId(),
                bond.getBondName(),
                bond.getFaceValue(),
                bond.getIssuePrice(),
                bond.getPurchasePrice(),
                bond.getIssueDate(),
                bond.getMaturityDate(),
                bond.getTotalPeriods(),
                bond.getRateType().name(),
                bond.getRateValue(),
                bond.getCapitalization() != null ? bond.getCapitalization().name() : null,
                bond.getFrequency().name(),
                bond.getGraceType().name(),
                bond.getGraceCapital(),
                bond.getGraceInterest(),
                bond.getCommission(),
                bond.getMarketRate()
        );
    }
}
