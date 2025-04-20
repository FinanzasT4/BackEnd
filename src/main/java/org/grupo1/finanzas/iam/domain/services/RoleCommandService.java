package org.grupo1.finanzas.iam.domain.services;

import org.grupo1.finanzas.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
