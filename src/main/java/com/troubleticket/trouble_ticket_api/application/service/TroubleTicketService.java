package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

import java.util.UUID;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;

public class TroubleTicketService implements CreateTroubleTicketUseCase {

    private final TroubleTicketRepository repository;

    public TroubleTicketService(TroubleTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public TroubleTicket create(TroubleTicketCreateRequest request) {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                request.getDescription()
        );

        return repository.save(ticket);
    }
}