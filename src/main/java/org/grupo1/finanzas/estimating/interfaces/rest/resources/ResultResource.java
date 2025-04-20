package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record ResultResource(Long id, Long bondId,
                             BigDecimal tcea, BigDecimal trea,
                             BigDecimal duration, BigDecimal durationMod,
                             BigDecimal convexity, BigDecimal maxMarketPrice,
                             List<PeriodResource> periods) {}