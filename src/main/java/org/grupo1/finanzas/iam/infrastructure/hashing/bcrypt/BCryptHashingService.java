package org.grupo1.finanzas.iam.infrastructure.hashing.bcrypt;

import org.grupo1.finanzas.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
