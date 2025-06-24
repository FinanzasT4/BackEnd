package org.grupo1.finanzas.estimating.application.internal.domainservices;

import org.grupo1.finanzas.estimating.domain.model.entities.CashFlowPeriod;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import org.grupo1.finanzas.estimating.domain.services.FinancialCalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class FinancialCalculatorServiceImpl implements FinancialCalculatorService {

    private static final int DEFAULT_PRECISION = 15;
    private static final int FINANCIAL_PRECISION = 8;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("PEN");

    @Override
    public CalculationResult calculate(BondParameters params) {
        List<CashFlowPeriod> CashFlow = generateCashFlow(params);
        FinancialMetrics metrics = calculateMetrics(params, CashFlow);
        return new CalculationResult(metrics, CashFlow);
    }

    private List<CashFlowPeriod> generateCashFlow(BondParameters params) {
        List<CashFlowPeriod> periods = new ArrayList<>();

        // 1. Obtener días por año según la convención del cupón
        int daysPerYear = getDaysPerYear(params.couponRate());

        // 2. Calcular la frecuencia de pago en días
        int paymentFrequencyInDays = getFrequencyInDays(params.frequency());

        // 3. Calcular el número de períodos por año
        double periodsPerYear = (double) daysPerYear / paymentFrequencyInDays;

        // 4. Calcular la Tasa Efectiva del Período (TES) a partir de la Tasa Cupón
        BigDecimal effectiveAnnualRate = params.couponRate().toEffectiveAnnualRate();
        BigDecimal tes = BigDecimal.valueOf(Math.pow(1 + effectiveAnnualRate.doubleValue(), 1.0 / periodsPerYear) - 1)
                .setScale(DEFAULT_PRECISION, ROUNDING_MODE);

        // 5. Generar el cronograma de pagos
        BigDecimal currentBalance = params.faceValue().amount();
        for (int i = 1; i <= params.totalPeriods(); i++) {
            BigDecimal initialBalance = currentBalance;
            BigDecimal interest = initialBalance.multiply(tes);

            GracePeriodState graceState = determineGraceState(i, params.gracePeriod());
            BigDecimal amortization = BigDecimal.ZERO;
            BigDecimal coupon;

            switch (graceState) {
                case TOTAL:
                    // En período de gracia total, el interés se capitaliza. No hay cupón.
                    currentBalance = initialBalance.add(interest);
                    coupon = BigDecimal.ZERO;
                    break;

                case PARTIAL:
                    // En período de gracia parcial, solo se paga el interés. No hay amortización.
                    coupon = interest;
                    break;

                case NONE:
                default:
                    // Período normal. Se calcula la cuota completa.
                    // Para un bono bullet (el más común), la amortización es cero hasta el final.
                    coupon = interest;
                    if (i == params.totalPeriods()) {
                        amortization = initialBalance;
                    }
                    break;
            }

            // El flujo de caja para el bonista es el cupón más la amortización.
            BigDecimal CashFlowForHolder = coupon.add(amortization);
            currentBalance = currentBalance.subtract(amortization);

            periods.add(new CashFlowPeriod(
                    i,
                    graceState,
                    new Money(initialBalance.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY),
                    new Money(interest.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY),
                    new Money(coupon.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY),
                    new Money(amortization.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY),
                    new Money(currentBalance.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY),
                    new Money(CashFlowForHolder.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), DEFAULT_CURRENCY)
            ));
        }

        return periods;
    }

    private FinancialMetrics calculateMetrics(BondParameters params, List<CashFlowPeriod> CashFlow) {
        // 1. Tasa de descuento de mercado por período
        BigDecimal marketRatePerPeriod = getMarketRatePerPeriod(params);

        // 2. Calcular el Precio Sucio (Valor Presente de los flujos de caja del bonista)
        BigDecimal dirtyPrice = calculateDirtyPrice(CashFlow, marketRatePerPeriod);

        // 3. Calcular Duración de Macaulay, Duración Modificada y Convexidad
        BigDecimal macaulayDurationInPeriods = calculateMacaulayDurationInPeriods(CashFlow, marketRatePerPeriod, dirtyPrice);
        BigDecimal modifiedDurationInPeriods = macaulayDurationInPeriods.divide(BigDecimal.ONE.add(marketRatePerPeriod), DEFAULT_PRECISION, ROUNDING_MODE);
        BigDecimal convexityInPeriods = calculateConvexityInPeriods(CashFlow, marketRatePerPeriod, dirtyPrice);

        // Convertir Duraciones a años
        double periodsPerYear = getPeriodsPerYear(params);
        BigDecimal macaulayDurationInYears = macaulayDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);
        BigDecimal modifiedDurationInYears = modifiedDurationInPeriods.divide(BigDecimal.valueOf(periodsPerYear), FINANCIAL_PRECISION, ROUNDING_MODE);

        // 4. Calcular la TREA (TIR del bonista) y la TCEA (TIR del emisor)
        BigDecimal trea = calculateIRR(getHolderCashFlow(params, CashFlow), 0.1, 100, 1e-10).multiply(BigDecimal.valueOf(periodsPerYear));
        BigDecimal tcea = calculateIRR(getIssuerCashFlow(params, CashFlow), 0.1, 100, 1e-10).multiply(BigDecimal.valueOf(periodsPerYear));

        // 5. Placeholder para Precio Limpio
        // Simplificación: Asumimos que no hay intereses corridos

        return new FinancialMetrics(
                tcea.setScale(FINANCIAL_PRECISION, ROUNDING_MODE),
                trea.setScale(FINANCIAL_PRECISION, ROUNDING_MODE),
                macaulayDurationInYears,
                modifiedDurationInYears,
                convexityInPeriods.setScale(FINANCIAL_PRECISION, ROUNDING_MODE), // Convexidad se deja en unidades de períodos^2
                new Money(dirtyPrice.setScale(FINANCIAL_PRECISION, ROUNDING_MODE)),
                new Money(dirtyPrice.setScale(FINANCIAL_PRECISION, ROUNDING_MODE))
        );
    }

// --- Métodos de Ayuda para el Cálculo de Métricas ---

    private BigDecimal getMarketRatePerPeriod(BondParameters params) {
        int daysPerYear = getDaysPerYear(params.couponRate());
        int paymentFrequencyInDays = getFrequencyInDays(params.frequency());
        double periodsPerYear = (double) daysPerYear / paymentFrequencyInDays;
        BigDecimal effectiveAnnualMarketRate = params.marketRate().toEffectiveAnnualRate();
        return BigDecimal.valueOf(Math.pow(1 + effectiveAnnualMarketRate.doubleValue(), 1.0 / periodsPerYear) - 1)
                .setScale(DEFAULT_PRECISION, ROUNDING_MODE);
    }

    private double getPeriodsPerYear(BondParameters params) {
        int daysPerYear = getDaysPerYear(params.couponRate());
        int paymentFrequencyInDays = getFrequencyInDays(params.frequency());
        return (double) daysPerYear / paymentFrequencyInDays;
    }

    private BigDecimal calculateDirtyPrice(List<CashFlowPeriod> CashFlow, BigDecimal discountRate) {
        BigDecimal presentValue = BigDecimal.ZERO;
        for (CashFlowPeriod period : CashFlow) {
            BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(period.number()), DEFAULT_PRECISION, ROUNDING_MODE);
            presentValue = presentValue.add(period.cashFlow().amount().multiply(pvFactor));
        }
        return presentValue;
    }

    private BigDecimal calculateMacaulayDurationInPeriods(List<CashFlowPeriod> CashFlow, BigDecimal discountRate, BigDecimal dirtyPrice) {
        BigDecimal weightedMaturitySum = BigDecimal.ZERO;
        for (CashFlowPeriod period : CashFlow) {
            BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(period.number()), DEFAULT_PRECISION, ROUNDING_MODE);
            BigDecimal presentValue = period.cashFlow().amount().multiply(pvFactor);
            weightedMaturitySum = weightedMaturitySum.add(presentValue.multiply(BigDecimal.valueOf(period.number())));
        }
        if (dirtyPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return weightedMaturitySum.divide(dirtyPrice, DEFAULT_PRECISION, ROUNDING_MODE);
    }

    private BigDecimal calculateConvexityInPeriods(List<CashFlowPeriod> CashFlow, BigDecimal discountRate, BigDecimal dirtyPrice) {
        BigDecimal convexitySum = BigDecimal.ZERO;
        BigDecimal discountFactorSquared = BigDecimal.ONE.add(discountRate).pow(2);
        for (CashFlowPeriod period : CashFlow) {
            BigDecimal pvFactor = BigDecimal.ONE.divide(BigDecimal.ONE.add(discountRate).pow(period.number()), DEFAULT_PRECISION, ROUNDING_MODE);
            BigDecimal presentValue = period.cashFlow().amount().multiply(pvFactor);
            BigDecimal t = BigDecimal.valueOf(period.number());
            BigDecimal t_squared_plus_t = t.multiply(t.add(BigDecimal.ONE));
            convexitySum = convexitySum.add(presentValue.multiply(t_squared_plus_t));
        }
        if (dirtyPrice.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return convexitySum.divide(dirtyPrice.multiply(discountFactorSquared), DEFAULT_PRECISION, ROUNDING_MODE);
    }

// --- Implementación de la Tasa Interna de Retorno (TIR / IRR) ---

    private List<BigDecimal> getHolderCashFlow(BondParameters params, List<CashFlowPeriod> CashFlow) {
        List<BigDecimal> holderFlows = new ArrayList<>();
        // Flujo inicial: el bonista paga el precio de compra. Es un flujo negativo.
        holderFlows.add(params.purchasePrice().amount().negate());
        CashFlow.forEach(p -> holderFlows.add(p.cashFlow().amount()));
        return holderFlows;
    }

    private List<BigDecimal> getIssuerCashFlow(BondParameters params, List<CashFlowPeriod> CashFlow) {
        List<BigDecimal> issuerFlows = new ArrayList<>();
        // Flujo inicial: el emisor recibe el precio de emisión, menos la comisión. Es un flujo positivo.
        BigDecimal commissionAmount = params.issuePrice().amount().multiply(params.commission().divide(BigDecimal.valueOf(100), DEFAULT_PRECISION, ROUNDING_MODE));
        issuerFlows.add(params.issuePrice().amount().subtract(commissionAmount));
        CashFlow.forEach(p -> issuerFlows.add(p.cashFlow().amount().negate())); // Los pagos del emisor son negativos
        return issuerFlows;
    }

    /**
     * Calcula la Tasa Interna de Retorno (TIR) usando el metodo de Newton-Raphson.
     * @param CashFlows Lista de flujos de caja, donde el primero es en el período 0.
     * @param guess Estimación inicial de la tasa (ej. 0.1 para 10%).
     * @param maxIterations Número máximo de iteraciones.
     * @param tolerance Tolerancia para la convergencia.
     * @return La TIR por período.
     */
    public BigDecimal calculateIRR(final List<BigDecimal> CashFlows, double guess, int maxIterations, double tolerance) {
        BigDecimal rate = new BigDecimal(guess);
        int i = 0;
        while (i < maxIterations) {
            BigDecimal npv = calculateNPV(CashFlows, rate);
            BigDecimal derivative = calculateNPVDerivative(CashFlows, rate);

            if (derivative.abs().compareTo(BigDecimal.valueOf(1e-15)) < 0) { // Evitar división por cero
                break;
            }

            BigDecimal newRate = rate.subtract(npv.divide(derivative, DEFAULT_PRECISION, ROUNDING_MODE));

            if (newRate.subtract(rate).abs().compareTo(BigDecimal.valueOf(tolerance)) < 0) {
                return newRate; // Convergencia alcanzada
            }
            rate = newRate;
            i++;
        }
        // Si no converge, se puede lanzar una excepción o devolver un valor indicativo.
        // throw new IllegalStateException("IRR calculation did not converge.");
        return rate; // Devuelve la mejor aproximación encontrada.
    }

    private BigDecimal calculateNPV(List<BigDecimal> CashFlows, BigDecimal rate) {
        BigDecimal npv = BigDecimal.ZERO;
        for (int t = 0; t < CashFlows.size(); t++) {
            npv = npv.add(CashFlows.get(t).divide(BigDecimal.ONE.add(rate).pow(t), DEFAULT_PRECISION, ROUNDING_MODE));
        }
        return npv;
    }

    private BigDecimal calculateNPVDerivative(List<BigDecimal> CashFlows, BigDecimal rate) {
        BigDecimal derivative = BigDecimal.ZERO;
        for (int t = 1; t < CashFlows.size(); t++) { // La derivada empieza en t=1
            BigDecimal term = CashFlows.get(t).multiply(BigDecimal.valueOf(-t));
            derivative = derivative.add(term.divide(BigDecimal.ONE.add(rate).pow(t + 1), DEFAULT_PRECISION, ROUNDING_MODE));
        }
        return derivative;
    }

    private int getDaysPerYear(InterestRate couponRate) {
        // La convención puede depender de la tasa (ej. 360 o 365 días)
        // Por ahora, asumimos 365.
        return 365;
    }

    private int getFrequencyInDays(Frequency frequency) {
        return switch (frequency) {
            case DAY -> 1;
            case FORTNIGHT -> 15;
            case MONTH -> 30; // Simplificación, se puede usar Period para más precisión
            case BIMONTHLY -> 60;
            case QUARTER -> 90;
            case FOUR_MONTHLY -> 120;
            case SEMESTER -> 180;
            case YEAR -> 365;
        };
    }

    private GracePeriodState determineGraceState(int currentPeriod, GracePeriod gracePeriod) {
        if (gracePeriod.type() == GraceType.TOTAL && currentPeriod <= gracePeriod.capitalPeriods()) {
            return GracePeriodState.TOTAL;
        }
        if (gracePeriod.type() == GraceType.PARTIAL && currentPeriod <= gracePeriod.capitalPeriods()) {
            return GracePeriodState.PARTIAL;
        }
        return GracePeriodState.NONE;
    }
}