package org.grupo1.finanzas.iam.domain.model.commands;

import org.grupo1.finanzas.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String username,
                            String password,
                            List<Role> roles) {
}
