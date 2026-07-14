package com.troubleticket.trouble_ticket_api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.Collections;

public class TenantAuthentication extends AbstractAuthenticationToken {

    private final String tenantId;

    public TenantAuthentication(String tenantId) {
        super(Collections.emptyList());
        this.tenantId = tenantId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }
}
