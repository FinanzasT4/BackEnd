package org.grupo1.finanzas.estimating.domain.model.commands;

import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBondCommand(
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
) {}
