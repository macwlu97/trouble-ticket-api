package com.troubleticket.trouble_ticket_api.domain.model.value;

public record TenantId(String value) {
    public TenantId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("TenantId cannot be empty");
    }
}