package org.grupo1.finanzas.iam.interfaces.rest.transform;

import org.grupo1.finanzas.iam.domain.model.aggregates.User;
import org.grupo1.finanzas.iam.domain.model.entities.Role;
import org.grupo1.finanzas.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream().map(Role::getStringName).toList();
        return new UserResource(user.getId(), user.getUsername(), roles);
    }
}
