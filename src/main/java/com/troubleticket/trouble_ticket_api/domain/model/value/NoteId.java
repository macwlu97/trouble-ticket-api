package com.troubleticket.trouble_ticket_api.domain.model.value;

import java.util.UUID;

public record NoteId(UUID value) {
    public NoteId {
        if (value == null) {
            throw new IllegalArgumentException("Note ID cannot be null");
        }
    }

    public static NoteId generate() {
        return new NoteId(UUID.randomUUID());
    }
}