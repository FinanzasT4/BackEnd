package org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.mappers;

import org.grupo1.finanzas.estimating.domain.model.aggregates.BondValuation;
import org.grupo1.finanzas.estimating.domain.model.entities.CashFlowPeriod;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.*;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.model.BondValuationJpaEntity;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.model.CashFlowPeriodJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BondValuationPersistenceMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt"),
            @Mapping(source = "valuationName", target = "valuationName"),
            @Mapping(source = "userId.id", target = "userId"),
            // Mapeos de Parámetros
            @Mapping(source = "parameters.faceValue.amount", target = "faceValueAmount"),
            @Mapping(source = "parameters.faceValue.currency", target = "faceValueCurrency"),
            @Mapping(source = "parameters.marketPrice.amount", target = "marketPriceAmount"),
            @Mapping(source = "parameters.marketPrice.currency", target = "marketPriceCurrency"),
            @Mapping(source = "parameters.issueDate", target = "issueDate"),
            @Mapping(source = "parameters.maturityDate", target = "maturityDate"),
            @Mapping(source = "parameters.totalPeriods", target = "totalPeriods"),
            @Mapping(source = "parameters.couponRate.value", target = "couponRateValue"),
            @Mapping(source = "parameters.couponRate.type", target = "couponRateType"),
            @Mapping(source = "parameters.couponRate.capitalization", target = "couponRateCapitalization"),
            @Mapping(source = "parameters.frequency", target = "frequency"),
            @Mapping(source = "parameters.gracePeriod.type", target = "graceType"),
            @Mapping(source = "parameters.gracePeriod.capitalPeriods", target = "graceCapitalPeriods"),
            @Mapping(source = "parameters.gracePeriod.interestPeriods", target = "graceInterestPeriods"),
            @Mapping(source = "parameters.marketRate.value", target = "marketRateValue"),
            // Mapeos de Costos (NUEVOS)
            @Mapping(source = "parameters.issuerStructuringCost", target = "issuerStructuringCost"),
            @Mapping(source = "parameters.issuerPlacementCost", target = "issuerPlacementCost"),
            @Mapping(source = "parameters.issuerCavaliCost", target = "issuerCavaliCost"),
            @Mapping(source = "parameters.investorSabCost", target = "investorSabCost"),
            @Mapping(source = "parameters.investorCavaliCost", target = "investorCavaliCost"),
            // Mapeos de Métricas
            @Mapping(source = "metrics.tcea", target = "tcea"),
            @Mapping(source = "metrics.trea", target = "trea"),
            @Mapping(source = "metrics.macaulayDuration", target = "macaulayDuration"),
            @Mapping(source = "metrics.modifiedDuration", target = "modifiedDuration"),
            @Mapping(source = "metrics.convexity", target = "convexity"),
            @Mapping(source = "metrics.dirtyPrice.amount", target = "dirtyPriceAmount"),
            @Mapping(source = "metrics.dirtyPrice.currency", target = "dirtyPriceCurrency"),
            @Mapping(source = "metrics.cleanPrice.amount", target = "cleanPriceAmount"),
            @Mapping(source = "metrics.cleanPrice.currency", target = "cleanPriceCurrency"),
            @Mapping(target = "cashFlow", source = "cashFlow")
    })
    BondValuationJpaEntity toJpaEntity(BondValuation domain);

    List<CashFlowPeriodJpaEntity> cashFlowPeriodListToJpaEntityList(List<CashFlowPeriod> list);
    default BondValuation toDomain(BondValuationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        BondParameters parameters = new BondParameters(
                new Money(jpaEntity.getFaceValueAmount(), jpaEntity.getFaceValueCurrency()),
                new Money(jpaEntity.getMarketPriceAmount(), jpaEntity.getMarketPriceCurrency()),
                jpaEntity.getIssueDate(),
                jpaEntity.getMaturityDate(),
                jpaEntity.getTotalPeriods(),
                new InterestRate(jpaEntity.getCouponRateValue(), jpaEntity.getCouponRateType(), Optional.ofNullable(jpaEntity.getCouponRateCapitalization())),
                jpaEntity.getFrequency(),
                new GracePeriod(jpaEntity.getGraceType(), jpaEntity.getGraceCapitalPeriods(), jpaEntity.getGraceInterestPeriods()),
                new InterestRate(jpaEntity.getMarketRateValue(), RateType.EFFECTIVE, Optional.empty()),
                jpaEntity.getIssuerStructuringCost(),
                jpaEntity.getIssuerPlacementCost(),
                jpaEntity.getIssuerCavaliCost(),
                jpaEntity.getInvestorSabCost(),
                jpaEntity.getInvestorCavaliCost()
        );

        FinancialMetrics metrics = null;
        if (jpaEntity.getTcea() != null) {
            metrics = new FinancialMetrics(
                    jpaEntity.getTcea(), jpaEntity.getTrea(), jpaEntity.getMacaulayDuration(),
                    jpaEntity.getModifiedDuration(), jpaEntity.getConvexity(),
                    new Money(jpaEntity.getDirtyPriceAmount(), jpaEntity.getDirtyPriceCurrency()),
                    new Money(jpaEntity.getCleanPriceAmount(), jpaEntity.getCleanPriceCurrency())
            );
        }

        BondValuation valuation = new BondValuation(
                jpaEntity.getValuationName(), new UserId(jpaEntity.getUserId()), parameters
        );

        valuation.setId(jpaEntity.getId());
        valuation.setCreatedAt(jpaEntity.getCreatedAt());
        valuation.setUpdatedAt(jpaEntity.getUpdatedAt());

        if (metrics != null && jpaEntity.getCashFlow() != null) {
            List<CashFlowPeriod> cashFlowDomainList = jpaEntity.getCashFlow().stream()
                    .map(this::cashFlowPeriodFromJpaEntity)
                    .collect(Collectors.toList());
            valuation.completeValuation(metrics, cashFlowDomainList);
        }

        return valuation;
    }

    /**
     * Implementación MANUAL y EXPLÍCITA para mapear un CashFlowPeriod de dominio a su entidad JPA.
     * Esto reemplaza la versión generada automáticamente que estaba incompleta.
     */
    default CashFlowPeriodJpaEntity cashFlowPeriodToJpaEntity(CashFlowPeriod domain) {
        if (domain == null) {
            return null;
        }

        CashFlowPeriodJpaEntity jpaEntity = new CashFlowPeriodJpaEntity();

        jpaEntity.setPeriodNumber(domain.number());
        jpaEntity.setGracePeriodState(domain.gracePeriodState());

        jpaEntity.setInitialBalanceAmount(domain.initialBalance().amount());
        jpaEntity.setInitialBalanceCurrency(domain.initialBalance().currency());

        jpaEntity.setInterestAmount(domain.interest().amount());
        jpaEntity.setInterestCurrency(domain.interest().currency());

        jpaEntity.setCouponAmount(domain.coupon().amount());
        jpaEntity.setCouponCurrency(domain.coupon().currency());

        jpaEntity.setAmortizationAmount(domain.amortization().amount());
        jpaEntity.setAmortizationCurrency(domain.amortization().currency());

        jpaEntity.setFinalBalanceAmount(domain.finalBalance().amount());
        jpaEntity.setFinalBalanceCurrency(domain.finalBalance().currency());

        jpaEntity.setCashFlowAmount(domain.cashFlow().amount());
        jpaEntity.setCashFlowCurrency(domain.cashFlow().currency());

        return jpaEntity;
    }

    default CashFlowPeriod cashFlowPeriodFromJpaEntity(CashFlowPeriodJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return new CashFlowPeriod(
                jpaEntity.getPeriodNumber(),
                jpaEntity.getGracePeriodState(),
                new Money(jpaEntity.getInitialBalanceAmount(), jpaEntity.getInitialBalanceCurrency()),
                new Money(jpaEntity.getInterestAmount(), jpaEntity.getInterestCurrency()),
                new Money(jpaEntity.getCouponAmount(), jpaEntity.getCouponCurrency()),
                new Money(jpaEntity.getAmortizationAmount(), jpaEntity.getAmortizationCurrency()),
                new Money(jpaEntity.getFinalBalanceAmount(), jpaEntity.getFinalBalanceCurrency()),
                new Money(jpaEntity.getCashFlowAmount(), jpaEntity.getCashFlowCurrency())
        );
    }
    default <T> T fromOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}