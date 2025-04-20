package org.grupo1.finanzas.estimating.application.internal.queryservices;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Bond;
import org.grupo1.finanzas.estimating.domain.model.queries.GetAllBondsQuery;
import org.grupo1.finanzas.estimating.domain.model.queries.GetBondByIdQuery;

import org.grupo1.finanzas.estimating.domain.services.BondQueryService;
import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories.BondRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BondQueryServiceImpl implements BondQueryService {

    private final BondRepository bondRepository;

    public BondQueryServiceImpl(BondRepository bondRepository) {
        this.bondRepository = bondRepository;
    }

    @Override
    public List<Bond> handle(GetAllBondsQuery query) {
        return bondRepository.findAll();
    }

    @Override
    public Optional<Bond> handle(GetBondByIdQuery query) {
        return bondRepository.findById(query.idBond());
    }
}
