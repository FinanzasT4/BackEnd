package org.grupo1.finanzas.estimating.domain.model.valueobjects;

public record UserId(Long id) {
    public UserId {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number.");
        }
    }
}