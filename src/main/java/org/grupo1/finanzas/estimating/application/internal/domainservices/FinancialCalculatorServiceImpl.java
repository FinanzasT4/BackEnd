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

    public static final int DEFAULT_PRECISION = 15;
    private static final int FINANCIAL_PRECISION = 8;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    // ===================================================================
    // == 1. METODO PRINCIPAL DE LA INTERFAZ ==
    // ===================================================================

    @Override
    public CalculationResult calculate(BondParameters params) {
        // --- Paso 1: Calcular tasas periódicas ---
        int periodsPerYear = getPeriodsPerYear(params.frequency());
        BigDecimal couponRatePerPeriod = getCouponRatePerPeriod(params.couponRate(), periodsPerYear);
        BigDecimal marketRatePerPeriod = getMarketRatePerPeriod(params.marketRate(), periodsPerYear);

        // --- Paso 2: Generar el flujo de caja ---
        List<CashFlowPeriod> cashFlow = generateCashFlow(params, couponRatePerPeriod);

        // --- Paso 3: Calcular todas las métricas financieras ---
        FinancialMetrics metrics = calculateMetrics(params, cashFlow, marketRatePerPeriod, periodsPerYear);

        return new CalculationResult(metrics, cashFlow);
    }

    // ===================================================================
    // == 2. MÉTODOS DE AYUDA PARA CÁLCULOS INTERNOS ==
    // ===================================================================

    // --- Conversión de Tasas y Frecuencias ---

    private int getPeriodsPerYear(Frequency frequency) {
        return switch (frequency) {
            case SEMESTER -> 2;
            case QUARTER -> 4;
            case MONTH -> 12;
            default -> 1; // Para YEAR
        };
    }

    private BigDecimal getCouponRatePerPeriod(InterestRate annualCouponRate, int periodsPerYear) {
        // Correcto: Llama a toEffectiveAnnualRate, que ya devuelve un decimal.
        BigDecimal effectiveAnnualRate = annualCouponRate.toEffectiveAnnualRate();
        return BigDecimal.valueOf(Math.pow(1 + effectiveAnnualRate.doubleValue(), 1.0 / periodsPerYear) - 1)
                .setScale(DEFAULT_PRECISION, ROUNDING_MODE);
    }

    private BigDecimal getMarketRatePerPeriod(InterestRate annualMarketRate, int periodsPerYear) {
        // Correcto: Llama a toEffectiveAnnualRate, que ya devuelve un decimal.
        BigDecimal effectiveAnnualRate = annualMarketRate.toEffectiveAnnualRate();
        return BigDecimal.valueOf(Math.pow(1 + effectiveAnnualRate.doubleValue(), 1.0 / periodsPerYear) - 1)
                .setScale(DEFAULT_PRECISION, ROUNDING_MODE);
    }

    // --- Generación del Flujo de Caja ---
    private List<CashFlowPeriod> generateCashFlow(BondParameters params, BigDecimal couponRatePerPeriod) {
        List<CashFlowPeriod> periods = new ArrayList<>();
        BigDecimal currentBalance = params.faceValue().amount();

        for (int i = 1; i <= params.totalPeriods(); i++) {
            BigDecimal initialBalance = currentBalance;
            BigDecimal interest = initialBalance.multiply(couponRatePerPeriod);
            BigDecimal coupon = interest;
            BigDecimal amortization = BigDecimal.ZERO;

            if (i == params.totalPeriods()) {
                amortization = initialBalance;
            }

            GracePeriodState graceState = determineGraceState(i, params.gracePeriod());

            if (graceState == GracePeriodState.TOTAL) {
                coupon = BigDecimal.ZERO;
            }

            BigDecimal cashFlowForHolder = coupon.add(amortization);
            BigDecimal finalBalance = currentBalance.subtract(amortization);

            periods.add(new CashFlowPeriod(
                    i, graceState, new Money(initialBalance), new Money(interest),
                    new Money(coupon), new Money(amortization),
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
        if (gracePeriod.type() == GraceType.PARTIAL && currentPeriod <= gracePeriod.capitalPeriods()) {
            // Para un bono bullet, la gracia parcial no tiene efecto en el cálculo del cupón (siempre es el interés)
            // pero es bueno marcarlo para claridad en la respuesta.
            return GracePeriodState.PARTIAL;
        }
        return GracePeriodState.NONE;
    }

    // --- Cálculo de Métricas Financieras ---

    private FinancialMetrics calculateMetrics(BondParameters params, List<CashFlowPeriod> cashFlow, BigDecimal marketRatePerPeriod, int periodsPerYear) {
        List<BigDecimal> holderFlowsList = cashFlow.stream().map(p -> p.cashFlow().amount()).collect(Collectors.toList());
        BigDecimal dirtyPrice = calculatePresentValue(holderFlowsList, marketRatePerPeriod);

        BigDecimal macaulayDurationInPeriods = calculateMacaulayDurationInPeriods(cashFlow, marketRatePerPeriod, dirtyPrice);
        BigDecimal macaulayDurationInYears = macaulayDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal modifiedDurationInPeriods = macaulayDurationInPeriods.divide(BigDecimal.ONE.add(marketRatePerPeriod), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal modifiedDurationInYears = modifiedDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal convexityInPeriods = calculateConvexityInPeriods(cashFlow, marketRatePerPeriod, dirtyPrice);
        BigDecimal convexityInYearsSq = convexityInPeriods.divide(BigDecimal.valueOf((long) periodsPerYear * periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        BigDecimal trea = calculateAnnualIRR(getHolderCashFlow(params, cashFlow), periodsPerYear);
        BigDecimal tcea = calculateAnnualIRR(getIssuerCashFlow(params, cashFlow), periodsPerYear);

        // Simplificación

        return new FinancialMetrics(
                tcea, trea, macaulayDurationInYears, modifiedDurationInYears,
                convexityInYearsSq, new Money(dirtyPrice), new Money(dirtyPrice)
        );
    }

    private BigDecimal calculatePresentValue(List<BigDecimal> flows, BigDecimal discountRate) {
        return IntStream.range(0, flows.size())
                .mapToObj(i -> flows.get(i).divide(BigDecimal.ONE.add(discountRate).pow(i + 1), DEFAULT_PRECISION, ROUNDING_MODE))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateMacaulayDurationInPeriods(List<CashFlowPeriod> cashFlow, BigDecimal discountRate, BigDecimal price) {
        BigDecimal weightedTimeSum = IntStream.range(0, cashFlow.size())
                .mapToObj(i -> {
                    BigDecimal periodTime = BigDecimal.valueOf(i + 1);
                    BigDecimal flow = cashFlow.get(i).cashFlow().amount();
                    BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(i + 1), DEFAULT_PRECISION, ROUNDING_MODE);
                    return flow.multiply(pvFactor).multiply(periodTime);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return price.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : weightedTimeSum.divide(price, DEFAULT_PRECISION, ROUNDING_MODE);
    }

    private BigDecimal calculateConvexityInPeriods(List<CashFlowPeriod> cashFlow, BigDecimal discountRate, BigDecimal price) {
        BigDecimal convexitySum = IntStream.range(0, cashFlow.size())
                .mapToObj(i -> {
                    BigDecimal t = BigDecimal.valueOf(i + 1);
                    BigDecimal flow = cashFlow.get(i).cashFlow().amount();
                    BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(i + 1), DEFAULT_PRECISION, ROUNDING_MODE);
                    return flow.multiply(pvFactor).multiply(t).multiply(t.add(BigDecimal.ONE));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (price.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return convexitySum.divide(price.multiply(BigDecimal.ONE.add(discountRate).pow(2)), DEFAULT_PRECISION, ROUNDING_MODE);
    }

    // --- Lógica de Tasa Interna de Retorno (TIR / IRR) ---

    private List<BigDecimal> getHolderCashFlow(BondParameters params, List<CashFlowPeriod> cashFlow) {
        List<BigDecimal> flows = new ArrayList<>();
        flows.add(params.purchasePrice().amount().negate());
        cashFlow.forEach(p -> flows.add(p.cashFlow().amount()));
        return flows;
    }

    private List<BigDecimal> getIssuerCashFlow(BondParameters params, List<CashFlowPeriod> cashFlow) {
        List<BigDecimal> flows = new ArrayList<>();
        BigDecimal commissionAmount = params.issuePrice().amount().multiply(params.commission().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE));
        flows.add(params.issuePrice().amount().subtract(commissionAmount));
        cashFlow.forEach(p -> flows.add(p.cashFlow().amount().negate()));
        return flows;
    }

    private BigDecimal calculateAnnualIRR(List<BigDecimal> flows, int periodsPerYear) {
        BigDecimal ratePerPeriod = calculateIRR(flows, 0.1, 100, 1e-10);
        // La fórmula para anualizar y multiplicar por 100 para mostrar como porcentaje es correcta.
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