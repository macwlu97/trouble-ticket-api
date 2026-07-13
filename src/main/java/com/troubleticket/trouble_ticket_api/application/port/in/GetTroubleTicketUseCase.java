package com.troubleticket.trouble_ticket_api.application.port.in;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

import java.util.List;
import java.util.UUID;

public interface GetTroubleTicketUseCase {

    TroubleTicket getById(UUID id);

    List<TroubleTicket> getAll();
}