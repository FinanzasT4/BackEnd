package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBondResource(
        Long bondId,
        String bondName,
        BigDecimal faceValue,
        BigDecimal issuePrice,
        BigDecimal purchasePrice,
        LocalDate issueDate,
        LocalDate maturityDate,
        int totalPeriods,
        RateType rateType,
        BigDecimal rateValue,
        Capitalization capitalization,
        Frequency frequency,
        GraceType graceType,
        int graceCapital,
        int graceInterest,
        BigDecimal commission,
        BigDecimal marketRate
) {

}
