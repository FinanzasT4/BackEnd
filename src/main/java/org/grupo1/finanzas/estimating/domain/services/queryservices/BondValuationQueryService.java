package org.grupo1.finanzas.estimating.domain.services.queryservices;

import org.grupo1.finanzas.estimating.application.internal.dtos.ValuationResponseDto;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllValuationsByUserIdQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetValuationByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Contrato para los casos de uso de lectura de BondValuation.
 */
public interface BondValuationQueryService {
    Optional<ValuationResponseDto> handle(GetValuationByIdQuery query);
    List<ValuationResponseDto> handle(GetAllValuationsByUserIdQuery query);
}