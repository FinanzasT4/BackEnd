package org.grupo1.finanzas.iam.application.internal.queryservices;

import org.grupo1.finanzas.iam.domain.model.entities.Role;
import org.grupo1.finanzas.iam.domain.model.queries.GetAllRolesQuery;
import org.grupo1.finanzas.iam.domain.model.queries.GetRoleByNameQuery;
import org.grupo1.finanzas.iam.domain.services.RoleQueryService;
import org.grupo1.finanzas.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleQueryServiceImpl  implements RoleQueryService {
    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> handle(GetAllRolesQuery query) {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> handle(GetRoleByNameQuery query) {
        return roleRepository.findByName(query.name());
    }
}
