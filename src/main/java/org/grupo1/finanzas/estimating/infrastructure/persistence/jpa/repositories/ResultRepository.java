package org.grupo1.finanzas.estimating.infrastructure.persistence.jpa.repositories;

import org.grupo1.finanzas.estimating.domain.model.aggregates.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
}
