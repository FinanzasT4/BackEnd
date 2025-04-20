package org.grupo1.finanzas.iam.interfaces.rest.transform;

import org.grupo1.finanzas.iam.domain.model.aggregates.User;
import org.grupo1.finanzas.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user,
                                                                 String token) {
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), token);
    }
}
