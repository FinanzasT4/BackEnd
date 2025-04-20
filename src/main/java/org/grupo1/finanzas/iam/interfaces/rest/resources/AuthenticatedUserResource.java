package org.grupo1.finanzas.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(Long id, String username, String token) {
}
