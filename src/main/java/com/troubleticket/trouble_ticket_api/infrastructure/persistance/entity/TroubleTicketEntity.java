package com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trouble_ticket")
public class TroubleTicketEntity {

    @Id
    private UUID id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TroubleTicketStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(
            mappedBy = "troubleTicket",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NoteEntity> notes = new ArrayList<>();

    protected TroubleTicketEntity() {
        // JPA
    }

    public TroubleTicketEntity(
            UUID id,
            String externalId,
            Long serviceId,
            String description,
            TroubleTicketStatus status,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void addNote(NoteEntity note) {
        notes.add(note);
        note.setTroubleTicket(this);
    }

    public void removeNote(NoteEntity note) {
        notes.remove(note);
        note.setTroubleTicket(null);
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

    public List<NoteEntity> getNotes() {
        return notes;
    }

    public void setStatus(TroubleTicketStatus status) {
        this.status = status;
    }
}