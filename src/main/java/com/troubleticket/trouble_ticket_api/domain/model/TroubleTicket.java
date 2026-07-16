package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.model.value.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TroubleTicket {

    private final TroubleTicketId id;
    private final TenantId tenantId;
    private final ExternalId externalId;
    private final ServiceId serviceId;
    private final String description;
    private TroubleTicketStatus status;
    private final OffsetDateTime createdAt;
    private final List<Note> notes;

    public static TroubleTicket createNew(
            TenantId tenantId,
            ExternalId externalId,
            ServiceId serviceId,
            String description,
            String initialNoteText
    ) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        TroubleTicketId generatedId = TroubleTicketId.generate();
        List<Note> initialNotes = new ArrayList<>();
        initialNotes.add(new Note(new NoteId(UUID.randomUUID()), initialNoteText, OffsetDateTime.now()));

        return new TroubleTicket(
                generatedId,
                tenantId,
                externalId,
                serviceId,
                description,
                new TroubleTicketStatus.New(),
                OffsetDateTime.now(),
                initialNotes
        );
    }

    public TroubleTicket(
            TroubleTicketId id,
            TenantId tenantId,
            ExternalId externalId,
            ServiceId serviceId,
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
        this.notes = new ArrayList<>(notes != null ? notes : Collections.emptyList());
    }

    public void acknowledge() {
        this.status = TroubleTicketStatus.fromString("acknowledged");
    }

    public void resolve() {
        this.status = TroubleTicketStatus.fromString("resolved");
    }

    public void close() {
        this.status = this.status.close();
    }

    public Note addNote(String text) {
        if (this.status instanceof TroubleTicketStatus.Closed) {
            throw new IllegalStateException("Cannot add notes to a closed ticket");
        }
        Note note = new Note(new NoteId(UUID.randomUUID()), text, OffsetDateTime.now());
        this.notes.add(note);
        return note;
    }

    public TroubleTicketId getId() { return id; }
    public TenantId getTenantId() { return tenantId; }
    public ExternalId getExternalId() { return externalId; }
    public ServiceId getServiceId() { return serviceId; }
    public String getDescription() { return description; }
    public TroubleTicketStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<Note> getNotes() { return Collections.unmodifiableList(notes); }
}