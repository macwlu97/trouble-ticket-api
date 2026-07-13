package com.troubleticket.trouble_ticket_api.infrastructure.persistance;

import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTroubleTicketRepository
        implements TroubleTicketRepository {

    private final Map<UUID, TroubleTicket> storage = new ConcurrentHashMap<>();

    @Override
    public TroubleTicket save(TroubleTicket ticket) {
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

}