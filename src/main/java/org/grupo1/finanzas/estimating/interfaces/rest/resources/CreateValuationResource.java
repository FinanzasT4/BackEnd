package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateValuationResource(
        @NotBlank @Size(min = 3, max = 50) String valuationName,
        @NotNull @Positive Long userId,
        @NotNull @DecimalMin(value = "0.01", message = "Face value must be positive") BigDecimal faceValue,
        @NotNull @DecimalMin(value = "0.01", message = "Purchase price must be positive") BigDecimal marketPrice,
        @NotNull LocalDate issueDate,
        @NotNull @Future(message = "Maturity date must be in the future") LocalDate maturityDate,
        @NotNull @Min(value = 1, message = "Total periods must be at least 1") int totalPeriods,
        @NotBlank String rateType,
        @NotNull @DecimalMin(value = "0.0", message = "Rate value cannot be negative") BigDecimal rateValue,
        String capitalization,
        @NotBlank String frequency,
        @NotBlank String graceType,
        @NotNull @Min(value = 0) int graceCapital,
        @NotNull @Min(value = 0) int graceInterest,
        @NotNull @DecimalMin(value = "0.0001", message = "Market rate must be positive") BigDecimal marketRate,

        @NotNull @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal issuerStructuringCost,

        @NotNull @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal issuerPlacementCost,

        @NotNull @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal issuerCavaliCost,

        @NotNull @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal investorSabCost,

        @NotNull @DecimalMin(value = "0.0", message = "Cost cannot be negative")
        BigDecimal investorCavaliCost
) {}