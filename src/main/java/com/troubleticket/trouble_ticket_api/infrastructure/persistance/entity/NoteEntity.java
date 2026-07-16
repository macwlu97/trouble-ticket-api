package com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "note")
public class NoteEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trouble_ticket_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private TroubleTicketEntity troubleTicket;

    protected NoteEntity() {
        // JPA
    }

    public NoteEntity(
            UUID id,
            String text,
            OffsetDateTime createdAt,
            TroubleTicketEntity troubleTicket
    ) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.troubleTicket = troubleTicket;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public TroubleTicketEntity getTroubleTicket() {
        return troubleTicket;
    }

    public void setTroubleTicket(TroubleTicketEntity troubleTicket) {
        this.troubleTicket = troubleTicket;
    }
}