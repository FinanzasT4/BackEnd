package org.grupo1.finanzas.iam.application.internal.commandservices;


import org.grupo1.finanzas.iam.domain.model.commands.SeedRolesCommand;
import org.grupo1.finanzas.iam.domain.model.entities.Role;
import org.grupo1.finanzas.iam.domain.model.valueobjects.Roles;
import org.grupo1.finanzas.iam.domain.services.RoleCommandService;
import org.grupo1.finanzas.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements RoleCommandService {
    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(
                role -> {
                    if (!roleRepository.existsByName(role)) {
                        roleRepository.save(new Role(Roles.valueOf(role.name())));
                    }
                }
        );
    }
}