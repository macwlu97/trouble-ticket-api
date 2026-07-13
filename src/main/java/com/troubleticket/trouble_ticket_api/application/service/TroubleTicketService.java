package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

import java.util.List;
import java.util.UUID;

public class TroubleTicketService
        implements CreateTroubleTicketUseCase,
        GetTroubleTicketUseCase {

    private final TroubleTicketRepository repository;

    public TroubleTicketService(TroubleTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public TroubleTicket create(TroubleTicketCreateRequest request) {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                request.getExternalId(),
                request.getServiceId(),
                request.getDescription()
        );

        if (request.getNote() != null && !request.getNote().isBlank()) {
            ticket.addNote(request.getNote());
        }

        return repository.save(ticket);
    }

    @Override
    public TroubleTicket getById(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Trouble Ticket not found: " + id));
    }

    @Override
    public List<TroubleTicket> getAll() {

        return repository.findAll();
    }
}