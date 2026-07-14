package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TroubleTicketService implements
        CreateTroubleTicketUseCase,
        GetTroubleTicketUseCase,
        CloseTroubleTicketUseCase,
        AddNoteUseCase {

    private final TroubleTicketRepository repository;

    public TroubleTicketService(TroubleTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TroubleTicket create(TroubleTicketCreateRequest request) {

        Optional<TroubleTicket> existing =
                repository.findByExternalId(request.getExternalId());

        if (existing.isPresent()) {
            return existing.get();
        }

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                request.getExternalId(),
                request.getServiceId(),
                request.getDescription()
        );

        if (request.getNote() != null &&
                !request.getNote().isBlank()) {

            ticket.addNote(request.getNote());
        }

        return repository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TroubleTicket getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TroubleTicket> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Note addNote(UUID ticketId, NoteCreateRequest request) {

        TroubleTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(ticketId));

        ticket.addNote(request.getText());

        repository.save(ticket);

        return ticket.getNotes().get(ticket.getNotes().size() - 1);
    }

    @Override
    @Transactional
    public TroubleTicket close(UUID id) {

        TroubleTicket ticket = repository.findById(id)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));

        ticket.close();

        return repository.save(ticket);
    }
}