package org.grupo1.finanzas.iam.domain.services;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.grupo1.finanzas.iam.domain.model.aggregates.User;
import org.grupo1.finanzas.iam.domain.model.commands.SignInCommand;
import org.grupo1.finanzas.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
}
