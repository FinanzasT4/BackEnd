package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import jakarta.validation.constraints.*; // Usamos validaciones aquí también
import java.math.BigDecimal;
import java.time.LocalDate;

// NOTA: Este record es idéntico en estructura a CreateBondValuationCommand,
// pero es una buena práctica mantenerlos separados. El Resource es el contrato
// PÚBLICO, mientras que el Command es un detalle de implementación INTERNO.
public record CreateValuationResource(
        @NotBlank String valuationName,
        @NotNull @Positive Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal faceValue,
        @NotNull @DecimalMin("0.01") BigDecimal issuePrice,
        @NotNull @DecimalMin("0.01") BigDecimal purchasePrice,
        @NotNull LocalDate issueDate,
        @NotNull LocalDate maturityDate,
        @NotNull @Min(1) int totalPeriods,
        @NotBlank String rateType, // Como String para flexibilidad en la API
        @NotNull @DecimalMin("0.0001") BigDecimal rateValue,
        String capitalization, // Puede ser null
        @NotBlank String frequency,
        @NotBlank String graceType,
        @NotNull @Min(0) int graceCapital,
        @NotNull @Min(0) int graceInterest,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal commission,
        @NotNull @DecimalMin("0.0001") BigDecimal marketRate
) {}