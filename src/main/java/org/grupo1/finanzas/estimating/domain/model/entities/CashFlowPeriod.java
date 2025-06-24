package org.grupo1.finanzas.estimating.domain.model.entities;

import org.grupo1.finanzas.estimating.domain.model.valueobjects.GracePeriodState;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Money;

/**
 * @param cashFlow Flujo para el bonista (cupón + amortización)
 */
public record CashFlowPeriod(int number, GracePeriodState gracePeriodState, Money initialBalance, Money interest,
                             Money coupon, Money amortization, Money finalBalance, Money cashFlow) {
}