package com.troubleticket.trouble_ticket_api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtTenantAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String TENANT_CLAIM_KEY = "tenant_id";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract tenant extraction payload directly from verified cryptographical token claims
        String tenantId = jwt.getClaimAsString(TENANT_CLAIM_KEY);

        if (tenantId == null || tenantId.isBlank()) {
            // Fallback strategy: use token issuer identifier as corporate tenant boundary anchor point
            tenantId = jwt.getIssuer().toString();
        }

        return new TenantAuthentication(tenantId);
    }
}
