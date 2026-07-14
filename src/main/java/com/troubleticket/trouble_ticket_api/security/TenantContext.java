package com.troubleticket.trouble_ticket_api.security;

public final class TenantContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenantId(String tenantId) {
        CURRENT.set(tenantId);
    }

    public static String getTenantId() {
        String tenantId = CURRENT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not initialized or missing in current execution thread");
        }
        return tenantId;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
