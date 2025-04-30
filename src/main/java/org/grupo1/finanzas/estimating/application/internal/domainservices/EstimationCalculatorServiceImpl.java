package org.grupo1.finanzas.estimating.application.internal.domainservices;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.grupo1.finanzas.estimating.domain.model.commands.CreateResultCommand;
import org.grupo1.finanzas.estimating.domain.model.entities.Period;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.GraceType;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.PeriodType;
import org.grupo1.finanzas.estimating.domain.services.EstimationCalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class EstimationCalculatorServiceImpl implements EstimationCalculatorService {

    @Override
    public Result calculateFrom(Bond bond, CreateResultCommand command) {
        List<Period> periods = new ArrayList<>();

        BigDecimal saldo = bond.getFaceValue();
        BigDecimal tea = bond.getRateValue().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // Obtener el tipo de periodo a partir de la frecuencia
        PeriodType periodType = PeriodType.fromFrequency(bond.getFrequency());

                   // Convertir 1 unidad del periodo a la unidad base deseada
            double conversionFactor = getConversionFactor(periodType);

            // Número de periodos por año
            double m = 1.0 / conversionFactor;

            // Tasa efectiva por periodo
            BigDecimal tes = BigDecimal.valueOf(
                    Math.pow(1 + tea.doubleValue(), 1.0 / m) - 1
            ).setScale(10, RoundingMode.HALF_UP);

            BigDecimal interesTotal = BigDecimal.ZERO;
            BigDecimal precioCompra = bond.getPurchasePrice();
            BigDecimal flujoTotal = BigDecimal.ZERO;

            for (int i = 1; i <= bond.getTotalPeriods(); i++) {
                BigDecimal interes = saldo.multiply(tes).setScale(2, RoundingMode.HALF_UP);
                BigDecimal amortizacion = BigDecimal.ZERO;
                BigDecimal cuota = interes;

                String tipoGracia = determineGracia(i, bond);
                if (tipoGracia.equals("T")) {
                    interes = BigDecimal.ZERO;
                    cuota = BigDecimal.ZERO;
                } else if (tipoGracia.equals("P")) {
                    cuota = interes;
                } else {
                    if (i == bond.getTotalPeriods()) {
                        amortizacion = saldo;
                        cuota = cuota.add(amortizacion);
                    }
                }

            BigDecimal saldoFinal = saldo.subtract(amortizacion);

            flujoTotal = flujoTotal.add(cuota);
            interesTotal = interesTotal.add(interes);

            periods.add(new Period(
                    null, i,
                    formatPercent(tea),
                    formatPercent(tes),
                    tipoGracia,
                    saldo,
                    interes.negate(),
                    cuota.negate(),
                    amortizacion.negate(),
                    saldoFinal
            ));

            saldo = saldoFinal;
        }

        // Cálculo de métricas
        BigDecimal tcea = flujoTotal.subtract(precioCompra).divide(precioCompra, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal trea = interesTotal.divide(precioCompra, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal duration = BigDecimal.valueOf(bond.getTotalPeriods()).multiply(BigDecimal.valueOf(0.5));
        BigDecimal durationMod = duration.divide(BigDecimal.ONE.add(tes), 4, RoundingMode.HALF_UP);
        BigDecimal convexity = duration.multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(0.5));
        BigDecimal maxMarketPrice = bond.getIssuePrice();

        Result result = new Result(command);
        result.initializeWith(
                periods,
                tcea.setScale(2, RoundingMode.HALF_UP),
                trea.setScale(2, RoundingMode.HALF_UP),
                duration.setScale(3, RoundingMode.HALF_UP),
                durationMod.setScale(3, RoundingMode.HALF_UP),
                convexity.setScale(2, RoundingMode.HALF_UP),
                maxMarketPrice.setScale(2, RoundingMode.HALF_UP)
        );
        return result;
    }

    private double getConversionFactor(PeriodType periodType) {
        return switch ("year".toLowerCase()) {
            case "day" -> periodType.toDays(1.0);
            case "fortnight" -> periodType.toFortnights(1.0);
            case "month" -> periodType.toMonths(1.0);
            case "bimonthly" -> periodType.toBimonths(1.0);
            case "quarter" -> periodType.toQuarters(1.0);
            case "four_monthly" -> periodType.toFourMonths(1.0);
            case "semester" -> periodType.toSemesters(1.0);
            case "year" -> periodType.toYears(1.0);
            default -> throw new IllegalArgumentException("Unsupported target unit: " + "year");
        };
    }

    private String determineGracia(int period, Bond bond) {
        GraceType type = bond.getGraceType();
        if (type == GraceType.TOTAL && period <= bond.getGraceCapital()) return "T";
        if (type == GraceType.PARTIAL && period <= bond.getGraceCapital()) return "P";
        return "N";
    }

    private String formatPercent(BigDecimal decimal) {
        return decimal.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + "%";
    }
}
