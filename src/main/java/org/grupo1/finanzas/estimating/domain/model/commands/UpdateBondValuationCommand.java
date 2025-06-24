package org.grupo1.finanzas.estimating.domain.model.commands;

import jakarta.validation.constraints.*;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Capitalization;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.Frequency;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBondValuationCommand(
        @NotNull @Positive Long valuationId,
        @NotBlank @Size(min = 3, max = 50) String valuationName,
        @NotNull @Positive Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal faceValue,
        @NotNull @DecimalMin("0.01") BigDecimal issuePrice,
        @NotNull @DecimalMin("0.01") BigDecimal purchasePrice,
        @NotNull @FutureOrPresent LocalDate issueDate,
        @NotNull @Future LocalDate maturityDate,
        @NotNull @Min(1) int totalPeriods,
        @NotNull RateType rateType,
        @NotNull @DecimalMin("0.0001") BigDecimal rateValue,
        Capitalization capitalization, // Puede ser null para tasa efectiva
        @NotNull Frequency frequency,
        @NotNull GraceType graceType,
        @NotNull @Min(0) int graceCapital,
        @NotNull @Min(0) int graceInterest,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal commission,
        @NotNull @DecimalMin("0.0001") BigDecimal marketRate
) {
    public UpdateBondValuationCommand {
        if (issueDate != null && maturityDate != null && !issueDate.isBefore(maturityDate)) {
            throw new IllegalArgumentException("Issue date must be before maturity date.");
        }
        if (rateType == RateType.NOMINAL && capitalization == null) {
            throw new IllegalArgumentException("Capitalization must be provided for a nominal rate.");
        }
    }
}