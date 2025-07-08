package org.grupo1.finanzas.estimating.domain.model.commands;

import jakarta.validation.constraints.*;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBondValuationCommand(
        @NotBlank @Size(min = 3, max = 50) String valuationName,
        @NotNull @Positive Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal faceValue,
        @NotNull @DecimalMin("0.01") BigDecimal marketPrice,
        @NotNull @FutureOrPresent LocalDate issueDate,
        @NotNull @Future LocalDate maturityDate,
        @NotNull @Min(1) int totalPeriods,
        @NotNull RateType rateType,
        @NotNull @DecimalMin("0.0001") BigDecimal rateValue,
        Capitalization capitalization,
        @NotNull Frequency frequency,
        @NotNull GraceType graceType,
        @NotNull @Min(0) int graceCapital,
        @NotNull @Min(0) int graceInterest,
        @NotNull @DecimalMin("0.0001") BigDecimal marketRate,

        @NotNull @DecimalMin("0.0") BigDecimal issuerStructuringCost,
        @NotNull @DecimalMin("0.0") BigDecimal issuerPlacementCost,
        @NotNull @DecimalMin("0.0") BigDecimal issuerCavaliCost,
        @NotNull @DecimalMin("0.0") BigDecimal investorSabCost,
        @NotNull @DecimalMin("0.0") BigDecimal investorCavaliCost
) {
    public CreateBondValuationCommand {
        if (issueDate != null && maturityDate != null && !issueDate.isBefore(maturityDate)) {
            throw new IllegalArgumentException("Issue date must be before maturity date.");
        }
        if (rateType == RateType.EFFECTIVE && capitalization == null) {
            throw new IllegalArgumentException("Capitalization must be provided for a effective rate.");
        }
    }
}