package com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trouble_ticket")
public class TroubleTicketEntity {

    @Id
    private String id; // CHANGED: UUID replaced with String for the custom TroubleTicketId format

    @Column(name = "tenant_id", nullable = false)
    private String tenantId; // Added for multi-tenancy core isolation anchor points

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(nullable = false)
    private String description;

    // CHANGED: Removed @Enumerated since status is now a polymorphic sealed interface.
    // We persist its value directly as a raw String database column.
    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @OneToMany(
            mappedBy = "troubleTicket",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<NoteEntity> notes = new ArrayList<>();

    protected TroubleTicketEntity() {
        // Required default constructor by JPA specification standard compliance
    }

    // Fully-parameterized constructor supporting tenant scope isolation data injection
    public TroubleTicketEntity(
            String id, // CHANGED: Swapped UUID with String
            String tenantId,
            String externalId,
            Long serviceId,
            String description,
            String status, // CHANGED: Swapped TroubleTicketStatus with raw String
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.tenantId = tenantId;
        this.externalId = externalId;
        this.serviceId = serviceId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void addNote(NoteEntity note) {
        notes.add(note);
        note.setTroubleTicket(this); // Maintains bidirectional relational mapping integrity
    }

    public void removeNote(NoteEntity note) {
        notes.remove(note);
        note.setTroubleTicket(null);
    }

    public String getId() { // CHANGED: Return type changed to String
        return id;
    }

    public String getTenantId() {
        return tenantId; // Added selector supporting multi-tenant core verification
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

    public String getStatus() { // CHANGED: Return type changed to String
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<NoteEntity> getNotes() {
        return notes;
    }

    public void setStatus(String status) { // CHANGED: Accept type changed to String
        this.status = status;
    }
}