package org.grupo1.finanzas.estimating.domain.model.commands;

public record DeleteBondByIdCommand(Long idBond) {
    public DeleteBondByIdCommand {
        if (idBond == null || idBond <= 0)
            throw new IllegalArgumentException("Bond ID must be positive");
    }
}
