package com.troubleticket.trouble_ticket_api.domain.model.value;

import java.util.UUID;

public record TroubleTicketId(String value) {
    public TroubleTicketId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Ticket ID cannot be empty");
        }
    }

    public static TroubleTicketId generate() {
        return new TroubleTicketId("TT-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
}