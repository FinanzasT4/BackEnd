package org.grupo1.finanzas.iam.interfaces.acl.services;

// package org.grupo1.finanzas.iam.interfaces.acl.services;

import lombok.RequiredArgsConstructor;
import org.grupo1.finanzas.iam.domain.model.queries.GetUserByIdQuery;
import org.grupo1.finanzas.iam.domain.services.UserQueryService;
import org.grupo1.finanzas.iam.interfaces.acl.IIamUserIdFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IamUserIdFacade implements IIamUserIdFacade {

    // Inyecta el servicio de consulta interno de IAM
    private final UserQueryService userQueryService;

    @Override
    public Optional<UserDto> fetchUserById(Long userId) {
        var query = new GetUserByIdQuery(userId);

        // Llama al servicio interno para obtener el Aggregate de dominio 'User'
        var userOptional = userQueryService.handle(query);

        // Si el usuario existe, lo mapea al DTO público 'UserDto'
        // Esto es importante: nunca se expone el Aggregate 'User' completo.
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        var user = userOptional.get();
        var userDto = new UserDto(user.getId(), user.getUsername());

        return Optional.of(userDto);
    }
}