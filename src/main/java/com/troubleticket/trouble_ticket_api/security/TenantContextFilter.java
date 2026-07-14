package com.troubleticket.trouble_ticket_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class TenantContextFilter extends OncePerRequestFilter {

    private static final String TENANT_CLAIM_KEY = "tenant_id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            if (authentication instanceof TenantAuthentication tenantAuth) {
                // Production flow via JwtTenantAuthenticationConverter
                TenantContext.setTenantId(tenantAuth.getTenantId());
            } else if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                // Test flow via MockMvc .with(jwt()) or default resource server authentication token object
                Jwt jwt = jwtAuth.getToken();
                String tenantId = jwt.getClaimAsString(TENANT_CLAIM_KEY);

                if (tenantId != null && !tenantId.isBlank()) {
                    TenantContext.setTenantId(tenantId);
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Guarantee thread-local cache sanitization after execution terminates
            TenantContext.clear();
        }
    }
}
