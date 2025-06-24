package org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories;

import org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.model.BondValuationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BondValuationJpaRepository extends JpaRepository<BondValuationJpaEntity, Long> {
    List<BondValuationJpaEntity> findByUserId(Long userId);
}
