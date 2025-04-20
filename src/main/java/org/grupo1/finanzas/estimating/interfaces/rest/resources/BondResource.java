// BondResource.java
package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BondResource(
        Long id,
        String bondName,
        BigDecimal faceValue,
        BigDecimal issuePrice,
        BigDecimal purchasePrice,
        LocalDate issueDate,
        LocalDate maturityDate,
        int totalPeriods,
        String rateType,
        BigDecimal rateValue,
        String capitalization,
        String frequency,
        String graceType,
        int graceCapital,
        int graceInterest,
        BigDecimal commission,
        BigDecimal marketRate
) {}
