package com.troubleticket.trouble_ticket_api.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Note {

    private final UUID id;

    private final String text;

    private final OffsetDateTime createdAt;

    public Note(UUID id,
                String text,
                OffsetDateTime createdAt) {

        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
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
}