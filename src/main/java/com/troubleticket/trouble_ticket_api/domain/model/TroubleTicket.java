package com.troubleticket.trouble_ticket_api.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TroubleTicket {

    private final UUID id;

    private final String externalId;

    private final Long serviceId;

    private final String description;

    private TroubleTicketStatus status;

    private final OffsetDateTime createdAt;

    private final List<Note> notes = new ArrayList<>();

    public TroubleTicket(
            UUID id,
            String externalId,
            Long serviceId,
            String description
    ) {
        this.id = id;
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.description = description;
        this.status = TroubleTicketStatus.NEW;
        this.createdAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Long getServiceId() {
        return serviceId;
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

    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    public Note addNote(String text) {

        Note note = new Note(
                UUID.randomUUID(),
                text,
                OffsetDateTime.now()
        );

        notes.add(note);

        return note;
    }

    public void acknowledge() {

        status = TroubleTicketStatus.ACKNOWLEDGED;
    }

    public void resolve() {

        status = TroubleTicketStatus.RESOLVED;
    }

    public void close() {

        if (status == TroubleTicketStatus.CLOSED) {
            return;
        }

        status = TroubleTicketStatus.CLOSED;
    }

}