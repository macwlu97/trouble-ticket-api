package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.exception.InvalidStatusTransitionException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TroubleTicket {

    private final UUID id;
    private final String tenantId; // Added for multi-tenancy core isolation
    private final String externalId;
    private final Long serviceId;
    private final String description;
    private TroubleTicketStatus status;
    private final OffsetDateTime createdAt;
    private final List<Note> notes = new ArrayList<>();

    // Creation constructor
    public TroubleTicket(
            UUID id,
            String tenantId,
            String externalId,
            Long serviceId,
            String description
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.description = description;
        this.status = TroubleTicketStatus.NEW;
        this.createdAt = OffsetDateTime.now();
    }

    // Reconstruction constructor (from database entity data mappings)
    public TroubleTicket(
            UUID id,
            String tenantId,
            String externalId,
            Long serviceId,
            String description,
            TroubleTicketStatus status,
            OffsetDateTime createdAt,
            List<Note> notes
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        if (notes != null) {
            this.notes.addAll(notes);
        }
    }

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getExternalId() { return externalId; }
    public Long getServiceId() { return serviceId; }
    public String getDescription() { return description; }
    public TroubleTicketStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<Note> getNotes() { return Collections.unmodifiableList(notes); }

    public Note addNote(String text) {
        Note note = new Note(UUID.randomUUID(), text, OffsetDateTime.now());
        notes.add(note);
        return note;
    }

    public void acknowledge() {
        this.status = TroubleTicketStatus.ACKNOWLEDGED;
    }

    public void resolve() {
        this.status = TroubleTicketStatus.RESOLVED;
    }

    public void close() {
        if (this.status == TroubleTicketStatus.CLOSED) {
            return;
        }
        // Business rule validation engine mapped from OpenAPI specifications contract requirements
        if (this.status == TroubleTicketStatus.NEW) {
            throw new InvalidStatusTransitionException(
                    String.format("Cannot transition status directly from '%s' to 'CLOSED' without processing stage", this.status)
            );
        }
        this.status = TroubleTicketStatus.CLOSED;
    }
}
