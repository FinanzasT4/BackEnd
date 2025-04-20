package org.grupo1.finanzas.estimating.domain.services;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllBondsQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetBondByIdQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetBondsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface BondQueryService {
    Optional<Bond> handle(GetBondByIdQuery query);

    List<Bond> handle(GetAllBondsQuery query);

    List<Bond> handle(GetBondsByUserIdQuery query);
}
