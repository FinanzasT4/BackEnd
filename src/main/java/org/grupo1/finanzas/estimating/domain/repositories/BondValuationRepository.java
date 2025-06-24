package org.grupo1.finanzas.estimating.domain.repositories;

import org.grupo1.finanzas.estimating.domain.model.aggregates.BondValuation;

import java.util.Optional;

public interface BondValuationRepository {
    BondValuation save(BondValuation valuation);
    Optional<BondValuation> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
}