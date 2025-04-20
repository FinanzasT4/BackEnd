package org.grupo1.finanzas.estimating.domain.model.commands;

import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondCommand(
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
        BigDecimal marketRate,
        Long userId // 👈 nuevo campo
) {
    public CreateBondCommand {
        if (bondName == null || bondName.isBlank())
            throw new IllegalArgumentException("Bond name cannot be null or blank");
        if (faceValue == null || faceValue.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Face value must be greater than 0");
        if (issuePrice == null || issuePrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Issue price must be greater than 0");
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Purchase price must be greater than 0");
        if (issueDate == null || maturityDate == null || !issueDate.isBefore(maturityDate))
            throw new IllegalArgumentException("Issue date must be before maturity date");
        if (totalPeriods <= 0)
            throw new IllegalArgumentException("Total periods must be greater than 0");
        if (rateType == null)
            throw new IllegalArgumentException("Rate type must not be null");
        if (rateValue == null || rateValue.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Rate value must be greater than 0");
        if (rateType == RateType.NOMINAL && capitalization == null)
            throw new IllegalArgumentException("Capitalization must be provided for nominal rate type");
        if (frequency == null)
            throw new IllegalArgumentException("Frequency must not be null");
        if (graceType == null)
            throw new IllegalArgumentException("Grace type must not be null");
        if (graceCapital < 0)
            throw new IllegalArgumentException("Grace capital cannot be negative");
        if (graceInterest < 0)
            throw new IllegalArgumentException("Grace interest cannot be negative");
        if (commission == null || commission.compareTo(BigDecimal.ZERO) < 0 || commission.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Commission must be between 0 and 100");
        if (marketRate == null || marketRate.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Market rate must be greater than 0");
        if (userId == null || userId <= 0)
            throw new IllegalArgumentException("User ID must be greater than 0");
    }
}
