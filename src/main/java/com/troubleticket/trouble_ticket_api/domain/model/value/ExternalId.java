package com.troubleticket.trouble_ticket_api.domain.model.value;

public record ExternalId(String value) {
    public ExternalId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExternalId cannot be empty");
        }
    }
}