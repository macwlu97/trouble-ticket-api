package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.model.value.NoteId;
import java.time.OffsetDateTime;

public record Note(
        NoteId id,
        String text,
        OffsetDateTime createdAt
) {
    public Note {
        if (id == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Note text cannot be empty");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Creation timestamp cannot be null");
        }
    }
}