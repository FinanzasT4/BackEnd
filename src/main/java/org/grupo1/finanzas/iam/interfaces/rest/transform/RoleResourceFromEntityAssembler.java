package org.grupo1.finanzas.iam.interfaces.rest.transform;

import org.grupo1.finanzas.iam.domain.model.entities.Role;
import org.grupo1.finanzas.iam.interfaces.rest.resources.RoleResource;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getStringName());

    }
}
