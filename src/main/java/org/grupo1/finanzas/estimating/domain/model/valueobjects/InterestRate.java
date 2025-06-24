package org.grupo1.finanzas.estimating.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public record InterestRate(BigDecimal value, RateType type, Optional<Capitalization> capitalization) {
    public InterestRate {
        // Reglas de negocio encapsuladas aquí
        if (type == RateType.NOMINAL && capitalization.isEmpty()) {
            throw new IllegalArgumentException("Capitalization is required for a nominal rate.");
        }
        if (type == RateType.EFFECTIVE && capitalization.isPresent()) {
            throw new IllegalArgumentException("Capitalization is not applicable for an effective rate.");
        }
    }

    // Ejemplo de lógica de negocio dentro del VO
    public BigDecimal toEffectiveAnnualRate() {
        if (type == RateType.EFFECTIVE) {
            return value; // Asumimos que la tasa efectiva de entrada es anual.
        }
        // Lógica de conversión de nominal a efectiva anual
        int m = capitalization.get().getPeriodsPerYear(); // Frecuencia de capitalización
        // TEA = (1 + TN/m)^m - 1
        BigDecimal ratePerPeriod = value.divide(BigDecimal.valueOf(m), 15, RoundingMode.HALF_UP);
        return BigDecimal.ONE.add(ratePerPeriod).pow(m).subtract(BigDecimal.ONE);
    }
}