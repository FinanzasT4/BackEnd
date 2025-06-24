package org.grupo1.finanzas.estimating.interfaces.acl.services;

import org.grupo1.finanzas.estimating.interfaces.acl.IIamContextFacade;
import org.grupo1.finanzas.iam.interfaces.acl.services.IamUserIdFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IamContextFacade implements IIamContextFacade {
    // Inyectamos la fachada que el contexto IAM ha decidido exponer públicamente.
    private final IamUserIdFacade iamUserIdFacade;

    public IamContextFacade(IamUserIdFacade iamUserIdFacade) {
        this.iamUserIdFacade = iamUserIdFacade;
    }

    @Override
    public Optional<UserDataDto> fetchUserById(Long userId) {
        // Llamamos al metodo expuesto por el contexto IAM.
        var userDtoFromIam = iamUserIdFacade.fetchUserById(userId);

        // Traducimos el DTO de IAM a nuestro DTO local de la ACL.
        // Esto es crucial para el desacoplamiento: si IAM cambia su DTO,
        // solo tenemos que actualizar este punto de mapeo.
        return userDtoFromIam.map(user ->
                new UserDataDto(user.userId(), user.username())
        );
    }
}
