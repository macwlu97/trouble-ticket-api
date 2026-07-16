package com.troubleticket.trouble_ticket_api.domain.model.value;

public record ServiceId(Long value) {
    public ServiceId {
        if (value == null || value < 1) {
            throw new IllegalArgumentException("ServiceId must be a positive number");
        }
    }
}