package com.troubleticket.trouble_ticket_api.application.port.out;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

public interface TroubleTicketRepository {

    TroubleTicket save(TroubleTicket troubleTicket);

}