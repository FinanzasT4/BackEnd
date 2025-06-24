package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import java.math.BigDecimal;

public record CashFlowPeriodResource(
        int number,
        String gracePeriodState,
        BigDecimal initialBalance,
        BigDecimal interest,
        BigDecimal coupon,
        BigDecimal amortization,
        BigDecimal finalBalance,
        BigDecimal cashflow
) {}
