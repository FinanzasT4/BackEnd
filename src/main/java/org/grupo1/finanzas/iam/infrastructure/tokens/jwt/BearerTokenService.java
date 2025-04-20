package org.grupo1.finanzas.iam.infrastructure.tokens.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.grupo1.finanzas.iam.application.internal.outboundservices.tokens.TokenService;

public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest token);

}
