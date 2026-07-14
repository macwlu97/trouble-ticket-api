package com.troubleticket.trouble_ticket_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class DevBearerTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Dynamically processes authorization exactly under openapi bearerAuth scheme rules
        if (header != null && header.startsWith("Bearer ")) {
            String tokenValue = header.substring(7).trim();

            // Fallback to demo name if the token string contains empty parameters
            String tenantId = (tokenValue.isEmpty()) ? "tenant-demo" : tokenValue;

            TenantContext.setTenantId(tenantId);

            var authentication = new TenantAuthentication(tenantId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Memory leak protection layer destroying thread caching post request termination
            TenantContext.clear();
        }
    }
}
