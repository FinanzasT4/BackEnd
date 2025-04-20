package org.grupo1.finanzas.iam.interfaces.rest.transform;

import org.grupo1.finanzas.iam.domain.model.commands.SignInCommand;
import org.grupo1.finanzas.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.username(), resource.password());
    }
}
