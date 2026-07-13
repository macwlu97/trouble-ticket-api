package com.troubleticket.trouble_ticket_api.infrastructure.persistance;

import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import org.springframework.stereotype.Repository;

import java.util.*;
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

    @Override
    public Optional<TroubleTicket> findById(UUID id) {

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TroubleTicket> findAll() {

        return new ArrayList<>(storage.values());
    }

}