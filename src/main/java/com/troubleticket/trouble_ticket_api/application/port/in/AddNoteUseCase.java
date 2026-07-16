package com.troubleticket.trouble_ticket_api.application.port.in;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.trouble_ticket_api.domain.model.Note;

import java.util.UUID;

public interface AddNoteUseCase {

    Note addNote(
            String ticketId,
            NoteCreateRequest request
    );
}