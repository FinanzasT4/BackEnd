package org.grupo1.finanzas.estimating.domain.model.queries;

public record GetBondByIdQuery(Long idBond) {
    public GetBondByIdQuery {
        if (idBond == null || idBond <= 0)
            throw new IllegalArgumentException("Bond ID must be positive");
    }
}
