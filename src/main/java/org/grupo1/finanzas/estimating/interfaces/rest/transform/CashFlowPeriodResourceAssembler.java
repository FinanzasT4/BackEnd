package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.domain.model.entities.CashFlowPeriod;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.CashFlowPeriodResource;

public class CashFlowPeriodResourceAssembler {
    public static CashFlowPeriodResource toResourceFromEntity(CashFlowPeriod entity) {
        if (entity == null) {
            return null;
        }
        return new CashFlowPeriodResource(
                entity.number(),
                entity.gracePeriodState().name(),
                entity.initialBalance().amount(),
                entity.interest().amount(),
                entity.coupon().amount(),
                entity.amortization().amount(),
                entity.finalBalance().amount(),
                entity.cashFlow().amount()
        );
    }
}