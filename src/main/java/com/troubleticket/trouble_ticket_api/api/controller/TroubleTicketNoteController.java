package com.troubleticket.trouble_ticket_api.api.controller;

import com.troubleticket.generated.api.TroubleTicketNoteApi;
import com.troubleticket.generated.model.Note;
import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.trouble_ticket_api.api.mapper.TroubleTicketMapper;
import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TroubleTicketNoteController
        implements TroubleTicketNoteApi {

    private final AddNoteUseCase addNoteUseCase;

    private final TroubleTicketMapper mapper;

    @Override
    public ResponseEntity<Note> addTroubleTicketNote(
            String id,
            NoteCreateRequest noteCreateRequest
    ) {

        var note = addNoteUseCase.addNote(
                UUID.fromString(id),
                noteCreateRequest
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        mapper.toApi(note)
                );
    }
}