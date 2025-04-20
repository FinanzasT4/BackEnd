package org.grupo1.finanzas.iam.domain.services;

import org.grupo1.finanzas.iam.domain.model.entities.Role;
import org.grupo1.finanzas.iam.domain.model.queries.GetAllRolesQuery;
import org.grupo1.finanzas.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
    Optional<Role> handle(GetRoleByNameQuery query);
}
