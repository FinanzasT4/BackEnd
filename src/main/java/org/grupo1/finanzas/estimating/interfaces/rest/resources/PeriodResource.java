package org.grupo1.finanzas.estimating.interfaces.rest.resources;

import java.math.BigDecimal;

public record PeriodResource(int number, String tea, String tes, String gracia,
                             BigDecimal saldoInicial, BigDecimal interes,
                             BigDecimal cuota, BigDecimal amortizacion,
                             BigDecimal saldoFinal) {}
