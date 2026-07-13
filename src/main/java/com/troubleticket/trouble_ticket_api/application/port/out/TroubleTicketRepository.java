package com.troubleticket.trouble_ticket_api.application.port.out;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TroubleTicketRepository {

    TroubleTicket save(TroubleTicket ticket);

    Optional<TroubleTicket> findById(UUID id);

    List<TroubleTicket> findAll();


}