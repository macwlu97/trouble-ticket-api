package com.troubleticket.trouble_ticket_api.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TroubleTicket {

    private final UUID id;

    private final String description;

    private TroubleTicketStatus status;

    private final OffsetDateTime createdAt;

    public TroubleTicket(UUID id,
                         String description) {

        this.id = id;
        this.description = description;
        this.status = TroubleTicketStatus.NEW;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TroubleTicketStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void acknowledge() {
        status = TroubleTicketStatus.ACKNOWLEDGED;
    }

    public void resolve() {
        status = TroubleTicketStatus.RESOLVED;
    }

    public void close() {
        status = TroubleTicketStatus.CLOSED;
    }
}