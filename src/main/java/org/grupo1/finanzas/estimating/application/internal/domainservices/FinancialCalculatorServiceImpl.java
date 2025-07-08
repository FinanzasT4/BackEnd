package org.grupo1.finanzas.estimating.application.internal.domainservices;

import org.grupo1.finanzas.estimating.domain.model.entities.CashFlowPeriod;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import org.grupo1.finanzas.estimating.domain.services.FinancialCalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FinancialCalculatorServiceImpl implements FinancialCalculatorService {

    public static final int DEFAULT_PRECISION = 16;
    private static final int FINANCIAL_PRECISION = 8;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public CalculationResult calculate(BondParameters params) {
        int periodsPerYear = getPeriodsPerYear(params.frequency());
        BigDecimal couponRatePerPeriod = convertToPeriodicRate(params.couponRate(), periodsPerYear);
        BigDecimal marketRatePerPeriod = convertToPeriodicRate(params.marketRate(), periodsPerYear);

        List<CashFlowPeriod> cashFlow = generateCashFlow(params, couponRatePerPeriod);

        FinancialMetrics metrics = calculateMetrics(params, cashFlow, marketRatePerPeriod, periodsPerYear);

        return new CalculationResult(metrics, cashFlow);
    }

    // ===================================================================
    // == 2. MÉTODOS DE AYUDA PARA CÁLCULOS INTERNOS ==
    // ===================================================================

    // --- Conversión de Tasas y Frecuencias (Refactorizado para claridad) ---

    private int getPeriodsPerYear(Frequency frequency) {
        return switch (frequency) {
            case SEMESTER -> 2;
            case QUARTER -> 4;
            case MONTH -> 12;
            case BIMONTHLY -> 6;
            case FOUR_MONTHLY -> 3;
            case FORTNIGHT -> 24;
            case DAY -> 360; // Usar base 360 como es común en finanzas
            default -> 1; // Para YEAR
        };
    }

    private BigDecimal convertToPeriodicRate(InterestRate annualRate, int periodsPerYear) {
        BigDecimal effectiveAnnualRate = annualRate.toEffectiveAnnualRate();
        double base = 1.0 + effectiveAnnualRate.doubleValue();
        double exponent = 1.0 / periodsPerYear;
        return BigDecimal.valueOf(Math.pow(base, exponent) - 1)
                .setScale(DEFAULT_PRECISION, ROUNDING_MODE);
    }

    // --- Generación del Flujo de Caja ---
    private List<CashFlowPeriod> generateCashFlow(BondParameters params, BigDecimal couponRatePerPeriod) {
        List<CashFlowPeriod> periods = new ArrayList<>();
        BigDecimal currentBalance = params.faceValue().amount();

        for (int i = 1; i <= params.totalPeriods(); i++) {
            BigDecimal initialBalance = currentBalance;
            BigDecimal interest = initialBalance.multiply(couponRatePerPeriod);
            BigDecimal amortization = BigDecimal.ZERO;
            BigDecimal couponPayment;

            GracePeriodState graceState = determineGraceState(i, params.gracePeriod());

            if (graceState == GracePeriodState.TOTAL) {
                // Durante gracia total, el pago del cupón es CERO.
                couponPayment = BigDecimal.ZERO;
                // El interés generado se capitaliza (se suma al saldo).
                currentBalance = currentBalance.add(interest);
            } else {
                // En gracia parcial o sin gracia, el pago del cupón es el interés generado.
                couponPayment = interest;
            }

            // La amortización solo ocurre en el último período para un bono bullet.
            if (i == params.totalPeriods()) {
                amortization = initialBalance;
            }

            BigDecimal cashFlowForHolder = couponPayment.add(amortization);
            BigDecimal finalBalance = currentBalance.subtract(amortization);

            periods.add(new CashFlowPeriod(
                    i, graceState, new Money(initialBalance), new Money(interest),
                    new Money(couponPayment), new Money(amortization),
                    new Money(finalBalance), new Money(cashFlowForHolder)
            ));

            currentBalance = finalBalance;
        }
        return periods;
    }


    private GracePeriodState determineGraceState(int currentPeriod, GracePeriod gracePeriod) {
        if (gracePeriod.type() == GraceType.TOTAL && currentPeriod <= gracePeriod.capitalPeriods()) {
            return GracePeriodState.TOTAL;
        }
        // Asumiendo que "gracia parcial" aplica hasta el final del periodo de gracia de capital
        if (gracePeriod.type() == GraceType.PARTIAL && currentPeriod <= gracePeriod.capitalPeriods()) {
            return GracePeriodState.PARTIAL;
        }
        return GracePeriodState.NONE;
    }

    // --- Cálculo de Métricas Financieras ---

    private FinancialMetrics calculateMetrics(BondParameters params, List<CashFlowPeriod> cashFlow, BigDecimal marketRatePerPeriod, int periodsPerYear) {
        List<BigDecimal> holderFlowsList = cashFlow.stream().map(p -> p.cashFlow().amount()).collect(Collectors.toList());
        BigDecimal bondPrice = calculatePresentValue(holderFlowsList, marketRatePerPeriod);

        BigDecimal macaulayDurationInPeriods = calculateMacaulayDurationInPeriods(cashFlow, marketRatePerPeriod, bondPrice);
        BigDecimal macaulayDurationInYears = macaulayDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal modifiedDurationInPeriods = macaulayDurationInPeriods.divide(BigDecimal.ONE.add(marketRatePerPeriod), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal modifiedDurationInYears = modifiedDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal convexityInPeriods = calculateConvexityInPeriods(cashFlow, marketRatePerPeriod, bondPrice);
        BigDecimal convexityInYearsSq = convexityInPeriods.divide(BigDecimal.valueOf((long) periodsPerYear * periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal trea = calculateAnnualIRR(getHolderCashFlowWithCosts(params, cashFlow), periodsPerYear);
        BigDecimal tcea = calculateAnnualIRR(getIssuerCashFlowWithCosts(params, cashFlow), periodsPerYear);

        return new FinancialMetrics(
                tcea, trea, macaulayDurationInYears, modifiedDurationInYears,
                convexityInYearsSq, new Money(bondPrice), new Money(bondPrice) // Distinguir 'dirty' y 'clean' price si se necesita
        );
    }

    private BigDecimal calculatePresentValue(List<BigDecimal> flows, BigDecimal discountRate) {
        return IntStream.range(0, flows.size())
                .mapToObj(i -> flows.get(i).divide(BigDecimal.ONE.add(discountRate).pow(i + 1), DEFAULT_PRECISION, ROUNDING_MODE))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMacaulayDurationInPeriods(List<CashFlowPeriod> cashFlow, BigDecimal discountRate, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal weightedTimeSum = IntStream.range(0, cashFlow.size())
                .mapToObj(i -> {
                    BigDecimal periodTime = BigDecimal.valueOf(i + 1);
                    BigDecimal flow = cashFlow.get(i).cashFlow().amount();
                    BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(i + 1), DEFAULT_PRECISION, ROUNDING_MODE);
                    return flow.multiply(pvFactor).multiply(periodTime);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return weightedTimeSum.divide(price, DEFAULT_PRECISION, ROUNDING_MODE);
    }

    private BigDecimal calculateConvexityInPeriods(List<CashFlowPeriod> cashFlow, BigDecimal discountRate, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        BigDecimal convexitySum = IntStream.range(0, cashFlow.size())
                .mapToObj(i -> {
                    BigDecimal t = BigDecimal.valueOf(i + 1);
                    BigDecimal flow = cashFlow.get(i).cashFlow().amount();
                    BigDecimal numerator = flow.multiply(t).multiply(t.add(BigDecimal.ONE));
                    BigDecimal denominator = BigDecimal.ONE.add(discountRate).pow(i + 3);
                    return numerator.divide(denominator, DEFAULT_PRECISION, ROUNDING_MODE);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return convexitySum.divide(price, DEFAULT_PRECISION, ROUNDING_MODE);
    }

    // --- Flujo de Caja con Costos para TIR (ACTUALIZADOS) ---

    /**
     * Calcula el flujo de caja completo para el inversionista, incluyendo el desembolso inicial
     * que considera el precio de mercado y los costos de transacción del inversionista.
     * @param params Parámetros del bono que incluyen los costos del inversionista.
     * @param cashFlow Flujo de caja futuro del bono (cupones y principal).
     * @return Lista de flujos de caja para el cálculo de la TREA.
     */
    private List<BigDecimal> getHolderCashFlowWithCosts(BondParameters params, List<CashFlowPeriod> cashFlow) {
        List<BigDecimal> flows = new ArrayList<>();

        // 1. Tomar los costos (en %) desde los parámetros y convertirlos a decimal.
        BigDecimal sabPct = params.investorSabCost().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal cavaliPct = params.investorCavaliCost().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE);

        // 2. Sumar los porcentajes de costo.
        BigDecimal totalCostPercentage = sabPct.add(cavaliPct);

        // 3. Calcular el desembolso total (Flujo 0) y añadirlo a la lista (negativo).
        BigDecimal totalInvestment = params.marketPrice().amount().multiply(BigDecimal.ONE.add(totalCostPercentage));
        flows.add(totalInvestment.negate());

        // 4. Añadir los flujos de caja futuros.
        cashFlow.forEach(p -> flows.add(p.cashFlow().amount()));

        return flows;
    }

    /**
     * Calcula el flujo de caja completo para el emisor, incluyendo el ingreso neto inicial
     * que considera el valor nominal y los costos de emisión.
     * @param params Parámetros del bono que incluyen los costos del emisor.
     * @param cashFlow Flujo de caja futuro del bono (cupones y principal).
     * @return Lista de flujos de caja para el cálculo de la TCEA.
     */
    private List<BigDecimal> getIssuerCashFlowWithCosts(BondParameters params, List<CashFlowPeriod> cashFlow) {
        List<BigDecimal> flows = new ArrayList<>();

        // 1. Tomar los costos (en %) desde los parámetros y convertirlos a decimal.
        BigDecimal structPct = params.issuerStructuringCost().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal placePct = params.issuerPlacementCost().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal cavaliPct = params.issuerCavaliCost().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE);

        // 2. Sumar los porcentajes de costo.
        BigDecimal totalCostPercentage = structPct.add(placePct).add(cavaliPct);

        // 3. Calcular el ingreso neto (Flujo 0) y añadirlo a la lista (positivo).
        BigDecimal totalCostAmount = params.faceValue().amount().multiply(totalCostPercentage);
        BigDecimal netProceeds = params.faceValue().amount().subtract(totalCostAmount);
        flows.add(netProceeds);

        // 4. Añadir los flujos de caja futuros (negativos para el emisor).
        cashFlow.forEach(p -> flows.add(p.cashFlow().amount().negate()));

        return flows;
    }

    // --- Lógica de Tasa Interna de Retorno (TIR / IRR) - Sin cambios, pero se asume correcta ---

    private BigDecimal calculateAnnualIRR(List<BigDecimal> flows, int periodsPerYear) {
        BigDecimal ratePerPeriod = calculateIRR(flows, 0.1, 100, 1e-10);
        return BigDecimal.ONE.add(ratePerPeriod).pow(periodsPerYear).subtract(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(100)).setScale(FINANCIAL_PRECISION, ROUNDING_MODE);
    }

    public BigDecimal calculateIRR(final List<BigDecimal> cashFlows, double guess, int maxIterations, double tolerance) {
        BigDecimal rate = new BigDecimal(guess);
        for (int i = 0; i < maxIterations; i++) {
            BigDecimal npv = calculateNPV(cashFlows, rate);
            BigDecimal derivative = calculateNPVDerivative(cashFlows, rate);
            if (derivative.abs().compareTo(BigDecimal.valueOf(1e-15)) < 0) break;
            BigDecimal newRate = rate.subtract(npv.divide(derivative, DEFAULT_PRECISION, ROUNDING_MODE));
            if (newRate.subtract(rate).abs().compareTo(BigDecimal.valueOf(tolerance)) < 0) return newRate;
            rate = newRate;
        }
        return rate;
    }

    private BigDecimal calculateNPV(List<BigDecimal> cashFlows, BigDecimal rate) {
        BigDecimal npv = BigDecimal.ZERO;
        for (int t = 0; t < cashFlows.size(); t++) {
            npv = npv.add(cashFlows.get(t).divide(BigDecimal.ONE.add(rate).pow(t), DEFAULT_PRECISION, ROUNDING_MODE));
        }
        return npv;
    }

    private BigDecimal calculateNPVDerivative(List<BigDecimal> cashFlows, BigDecimal rate) {
        BigDecimal derivative = BigDecimal.ZERO;
        for (int t = 1; t < cashFlows.size(); t++) {
            BigDecimal term = cashFlows.get(t).multiply(BigDecimal.valueOf(-t));
            derivative = derivative.add(term.divide(BigDecimal.ONE.add(rate).pow(t + 1), DEFAULT_PRECISION, ROUNDING_MODE));
        }
        return derivative;
    }
}