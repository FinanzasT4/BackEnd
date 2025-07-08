package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Resource que representa la respuesta completa de una valoración para la API.
 * Contiene todos los datos de entrada y de salida.
 */
public record ValuationResource(
        // --- Datos de Identificación ---
        Long id,
        String valuationName,
        Long userId,

        // --- Resultados Calculados (Métricas) ---
        BigDecimal tcea,
        BigDecimal trea,
        BigDecimal macaulayDurationInYears,
        BigDecimal modifiedDurationInYears,
        BigDecimal convexity,
        BigDecimal dirtyPrice,
        BigDecimal cleanPrice,

        // --- Parámetros de Entrada (Aplanados para conveniencia del cliente) ---
        BigDecimal faceValue,
        BigDecimal marketPrice,
        LocalDate issueDate,
        LocalDate maturityDate,
        Integer totalPeriods,
        String rateType,
        BigDecimal rateValue,
        String capitalization,
        String frequency,
        String graceType,
        Integer graceCapital,
        Integer graceInterest,
        BigDecimal marketRate,

        // --- Flujo de Caja Detallado ---
        List<CashFlowPeriodResource> cashFlow
) {}