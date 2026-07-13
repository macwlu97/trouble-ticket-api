package com.troubleticket.trouble_ticket_api.application.port.in;

public interface CreateTroubleTicketUseCase {

    com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket create(
            com.troubleticket.generated.model.TroubleTicketCreateRequest request
    );

}