package org.grupo1.finanzas.estimating.interfaces.rest.transform;

import org.grupo1.finanzas.estimating.application.internal.dtos.ValuationResponseDto;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.BondParameters;
import org.grupo1.finanzas.estimating.domain.model.valueobjects.FinancialMetrics;
import org.grupo1.finanzas.estimating.interfaces.rest.resources.ValuationResource;

public class ValuationResourceAssembler {
    public static ValuationResource toResourceFromDto(ValuationResponseDto dto) {
        if (dto == null) {
            return null;
        }

        BondParameters params = dto.parameters();
        FinancialMetrics metrics = dto.metrics();

        var cashFlowResources = dto.cashFlow().stream()
                .map(CashFlowPeriodResourceAssembler::toResourceFromEntity)
                .toList();

        // CONSTRUIMOS EL RESOURCE COMPLETO CON TODOS LOS CAMPOS
        return new ValuationResource(
                // --- Datos de Identificación ---
                dto.id(),
                dto.valuationName(),
                dto.userId(),

                // --- Resultados Calculados ---
                metrics.tcea(),
                metrics.trea(),
                metrics.macaulayDuration(),
                metrics.modifiedDuration(),
                metrics.convexity(),
                metrics.dirtyPrice().amount(),
                metrics.cleanPrice().amount(),

                // --- Parámetros de Entrada ---
                params.faceValue().amount(),
                params.marketPrice().amount(),
                params.issueDate(),
                params.maturityDate(),
                params.totalPeriods(),
                params.couponRate().type().name(),
                params.couponRate().value(),
                params.couponRate().capitalization().map(Enum::name).orElse(null),
                params.frequency().name(),
                params.gracePeriod().type().name(),
                params.gracePeriod().capitalPeriods(),
                params.gracePeriod().interestPeriods(),
                params.marketRate().value(),

                // --- Flujo de Caja ---
                cashFlowResources
        );
    }
}