package com.troubleticket.trouble_ticket_api.application.port.in;

import java.util.UUID;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

public interface CloseTroubleTicketUseCase {

    TroubleTicket close(UUID id);
}