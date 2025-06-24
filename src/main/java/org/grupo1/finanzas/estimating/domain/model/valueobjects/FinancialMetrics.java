package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import java.math.BigDecimal;

public record FinancialMetrics(
        BigDecimal tcea, BigDecimal trea,
        BigDecimal macaulayDuration, BigDecimal modifiedDuration,
        BigDecimal convexity, Money dirtyPrice, Money cleanPrice
) {}