package org.grupo1.finanzas.iam.domain.model.queries;

import org.grupo1.finanzas.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}